package com.robogyaan.invoice.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class InvoiceHistoryItem(
    val id: String,
    val promptDescription: String,
    val invoiceNo: String,
    val clientName: String,
    val totalAmount: Double,
    val invoiceData: InvoiceData,
    val createdAt: String
)

object FirebaseFirestoreService {

    private const val PROJECT_ID = "robogyaan-invoice"
    private const val API_KEY = "AIzaSyDu4Cow7uyLC-pUlmslehzI59gK-luFNbg"
    private const val COLLECTION = "invoice_prompts"
    private const val PREFS_NAME = "robogyaan_firestore_cache"
    private const val PREFS_KEY = "cached_prompts"

    private const val BASE_URL =
        "https://firestore.googleapis.com/v1/projects/$PROJECT_ID/databases/(default)/documents/$COLLECTION"

    suspend fun fetchHistory(context: Context): List<InvoiceHistoryItem> = withContext(Dispatchers.IO) {
        val cloudList = mutableListOf<InvoiceHistoryItem>()
        try {
            val url = URL("$BASE_URL?key=$API_KEY")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
            }

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = reader.readText()
                reader.close()

                val root = JSONObject(response)
                if (root.has("documents")) {
                    val docs = root.getJSONArray("documents")
                    for (i in 0 until docs.length()) {
                        val doc = docs.getJSONObject(i)
                        val name = doc.optString("name")
                        val id = name.substringAfterLast("/")
                        val fields = doc.optJSONObject("fields") ?: JSONObject()

                        val prompt = fields.optJSONObject("promptDescription")?.optString("stringValue") ?: "Saved Invoice"
                        val invoiceNo = fields.optJSONObject("invoiceNo")?.optString("stringValue") ?: "N/A"
                        val clientName = fields.optJSONObject("clientName")?.optString("stringValue") ?: "Client"
                        val totalAmount = fields.optJSONObject("totalAmount")?.optDouble("doubleValue")
                            ?: fields.optJSONObject("totalAmount")?.optDouble("integerValue")
                            ?: 0.0
                        val createdAt = fields.optJSONObject("createdAt")?.optString("stringValue") ?: ""

                        val dataMap = fields.optJSONObject("invoiceData")?.optJSONObject("mapValue")?.optJSONObject("fields")
                        val parsedInvoice = if (dataMap != null) {
                            parseInvoiceFromFirestoreFields(dataMap, invoiceNo, clientName)
                        } else {
                            InvoiceData(
                                invoiceNo = invoiceNo,
                                billedTo = BilledParty(name = clientName)
                            )
                        }

                        cloudList.add(
                            InvoiceHistoryItem(
                                id = id,
                                promptDescription = prompt,
                                invoiceNo = invoiceNo,
                                clientName = clientName,
                                totalAmount = totalAmount,
                                invoiceData = parsedInvoice,
                                createdAt = createdAt
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (cloudList.isNotEmpty()) {
            cacheHistory(context, cloudList)
            return@withContext cloudList
        }

        return@withContext getCachedHistory(context)
    }

    suspend fun savePrompt(
        context: Context,
        invoiceData: InvoiceData,
        promptDescription: String
    ): Boolean = withContext(Dispatchers.IO) {
        val newId = "inv_${System.currentTimeMillis()}"

        try {
            val url = URL("$BASE_URL?key=$API_KEY")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connectTimeout = 8000
                readTimeout = 8000
            }

            val fields = JSONObject().apply {
                put("promptDescription", JSONObject().put("stringValue", promptDescription))
                put("invoiceNo", JSONObject().put("stringValue", invoiceData.invoiceNo))
                put("clientName", JSONObject().put("stringValue", invoiceData.billedTo.name))
                put("totalAmount", JSONObject().put("doubleValue", invoiceData.totalAmount))
                put("createdAt", JSONObject().put("stringValue", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())))
                put("invoiceData", JSONObject().put("mapValue", JSONObject().put("fields", serializeInvoiceToFirestoreFields(invoiceData))))
            }
            val body = JSONObject().put("fields", fields)

            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(body.toString())
            writer.flush()
            writer.close()

            val code = conn.responseCode
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Always save to local cache for instant offline access
        val cached = getCachedHistory(context).toMutableList()
        val newItem = InvoiceHistoryItem(
            id = newId,
            promptDescription = promptDescription,
            invoiceNo = invoiceData.invoiceNo,
            clientName = invoiceData.billedTo.name,
            totalAmount = invoiceData.totalAmount,
            invoiceData = invoiceData,
            createdAt = java.text.SimpleDateFormat("dd MMM, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        )
        cached.add(0, newItem)
        cacheHistory(context, cached)

        return@withContext true
    }

    suspend fun deletePrompt(context: Context, id: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/$id?key=$API_KEY")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "DELETE"
                connectTimeout = 8000
                readTimeout = 8000
            }
            conn.responseCode
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val cached = getCachedHistory(context).filterNot { it.id == id }
        cacheHistory(context, cached)
        return@withContext true
    }

    suspend fun updatePrompt(
        context: Context,
        id: String,
        invoiceData: InvoiceData,
        promptDescription: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/$id?key=$API_KEY")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "PATCH"
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connectTimeout = 8000
                readTimeout = 8000
            }

            val fields = JSONObject().apply {
                put("promptDescription", JSONObject().put("stringValue", promptDescription))
                put("invoiceNo", JSONObject().put("stringValue", invoiceData.invoiceNo))
                put("clientName", JSONObject().put("stringValue", invoiceData.billedTo.name))
                put("totalAmount", JSONObject().put("doubleValue", invoiceData.totalAmount))
                put("updatedAt", JSONObject().put("stringValue", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())))
                put("invoiceData", JSONObject().put("mapValue", JSONObject().put("fields", serializeInvoiceToFirestoreFields(invoiceData))))
            }
            val body = JSONObject().put("fields", fields)

            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(body.toString())
            writer.flush()
            writer.close()

            conn.responseCode
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Update in local cache
        val cached = getCachedHistory(context).toMutableList()
        val index = cached.indexOfFirst { it.id == id }
        val updatedItem = InvoiceHistoryItem(
            id = id,
            promptDescription = promptDescription,
            invoiceNo = invoiceData.invoiceNo,
            clientName = invoiceData.billedTo.name,
            totalAmount = invoiceData.totalAmount,
            invoiceData = invoiceData,
            createdAt = java.text.SimpleDateFormat("dd MMM, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        )
        if (index != -1) {
            cached[index] = updatedItem
        } else {
            cached.add(0, updatedItem)
        }
        cacheHistory(context, cached)

        return@withContext true
    }

    private fun serializeInvoiceToFirestoreFields(data: InvoiceData): JSONObject {
        val root = JSONObject()
        root.put("invoiceNo", JSONObject().put("stringValue", data.invoiceNo))
        root.put("registrationNo", JSONObject().put("stringValue", data.registrationNo))
        root.put("issueDate", JSONObject().put("stringValue", data.issueDate))
        root.put("dueDate", JSONObject().put("stringValue", data.dueDate))

        val billedToFields = JSONObject().apply {
            put("name", JSONObject().put("stringValue", data.billedTo.name))
            put("address", JSONObject().put("stringValue", data.billedTo.address))
            put("pinState", JSONObject().put("stringValue", data.billedTo.pinState))
        }
        root.put("billedTo", JSONObject().put("mapValue", JSONObject().put("fields", billedToFields)))

        val senderFields = JSONObject().apply {
            put("company", JSONObject().put("stringValue", data.from.company))
            put("address", JSONObject().put("stringValue", data.from.address))
            put("cityPinState", JSONObject().put("stringValue", data.from.cityPinState))
        }
        root.put("from", JSONObject().put("mapValue", JSONObject().put("fields", senderFields)))

        val itemsArray = JSONArray()
        data.items.forEach { item ->
            val itemFields = JSONObject().apply {
                put("id", JSONObject().put("stringValue", item.id))
                put("description", JSONObject().put("stringValue", item.description))
                put("amountPerHead", JSONObject().put("doubleValue", item.amountPerHead))
                put("studentCount", JSONObject().put("integerValue", item.studentCount.toString()))
            }
            itemsArray.put(JSONObject().put("mapValue", JSONObject().put("fields", itemFields)))
        }
        root.put("items", JSONObject().put("arrayValue", JSONObject().put("values", itemsArray)))
        root.put("paymentMethod", JSONObject().put("stringValue", data.paymentMethod.name))
        root.put("authoriserName", JSONObject().put("stringValue", data.authoriserName))
        root.put("showWatermark", JSONObject().put("booleanValue", data.showWatermark))
        return root
    }

    private fun parseInvoiceFromFirestoreFields(fields: JSONObject, fallbackInv: String, fallbackClient: String): InvoiceData {
        val invNo = fields.optJSONObject("invoiceNo")?.optString("stringValue") ?: fallbackInv
        val regNo = fields.optJSONObject("registrationNo")?.optString("stringValue") ?: "273744"
        val issue = fields.optJSONObject("issueDate")?.optString("stringValue") ?: "01/09/2026"
        val due = fields.optJSONObject("dueDate")?.optString("stringValue") ?: "08/09/2026"

        val billedFields = fields.optJSONObject("billedTo")?.optJSONObject("mapValue")?.optJSONObject("fields")
        val billed = BilledParty(
            name = billedFields?.optJSONObject("name")?.optString("stringValue") ?: fallbackClient,
            address = billedFields?.optJSONObject("address")?.optString("stringValue") ?: "Tematha , Sonarpur",
            pinState = billedFields?.optJSONObject("pinState")?.optString("stringValue") ?: "Pin:743330 , West Bengal"
        )

        val senderFields = fields.optJSONObject("from")?.optJSONObject("mapValue")?.optJSONObject("fields")
        val sender = SenderParty(
            company = senderFields?.optJSONObject("company")?.optString("stringValue") ?: "ROBOGYAAN",
            address = senderFields?.optJSONObject("address")?.optString("stringValue") ?: "Dakshin Gobindopur , Sonarpur",
            cityPinState = senderFields?.optJSONObject("cityPinState")?.optString("stringValue") ?: "Kolkata - 700145 , West Bengal"
        )

        val itemsList = mutableListOf<InvoiceItem>()
        val itemsValues = fields.optJSONObject("items")?.optJSONObject("arrayValue")?.optJSONArray("values")
        if (itemsValues != null) {
            for (j in 0 until itemsValues.length()) {
                val itmObj = itemsValues.getJSONObject(j).optJSONObject("mapValue")?.optJSONObject("fields")
                if (itmObj != null) {
                    val id = itmObj.optJSONObject("id")?.optString("stringValue") ?: "item_$j"
                    val desc = itmObj.optJSONObject("description")?.optString("stringValue") ?: ""
                    val rate = itmObj.optJSONObject("amountPerHead")?.optDouble("doubleValue")
                        ?: itmObj.optJSONObject("amountPerHead")?.optDouble("integerValue")
                        ?: 0.0
                    val count = itmObj.optJSONObject("studentCount")?.optInt("integerValue")
                        ?: itmObj.optJSONObject("studentCount")?.optString("integerValue")?.toIntOrNull()
                        ?: 0
                    itemsList.add(InvoiceItem(id = id, description = desc, amountPerHead = rate, studentCount = count))
                }
            }
        }
        if (itemsList.isEmpty()) {
            itemsList.add(InvoiceItem(description = "Robogyaan ECA Programme", amountPerHead = 200.0, studentCount = 100))
        }

        val payStr = fields.optJSONObject("paymentMethod")?.optString("stringValue") ?: "CASH"
        val payment = try { PaymentMethod.valueOf(payStr) } catch (_: Exception) { PaymentMethod.CASH }
        val auth = fields.optJSONObject("authoriserName")?.optString("stringValue") ?: "Suman Mondal"
        val watermark = fields.optJSONObject("showWatermark")?.optBoolean("booleanValue", true) ?: true

        return InvoiceData(
            invoiceNo = invNo,
            registrationNo = regNo,
            issueDate = issue,
            dueDate = due,
            billedTo = billed,
            from = sender,
            items = itemsList,
            paymentMethod = payment,
            authoriserName = auth,
            showWatermark = watermark
        )
    }

    private fun cacheHistory(context: Context, items: List<InvoiceHistoryItem>) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val array = JSONArray()
            items.forEach { item ->
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("prompt", item.promptDescription)
                    put("invoiceNo", item.invoiceNo)
                    put("client", item.clientName)
                    put("amount", item.totalAmount)
                    put("createdAt", item.createdAt)
                    put("invoiceJson", invoiceToJson(item.invoiceData))
                }
                array.put(obj)
            }
            prefs.edit().putString(PREFS_KEY, array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCachedHistory(context: Context): List<InvoiceHistoryItem> {
        val list = mutableListOf<InvoiceHistoryItem>()
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val raw = prefs.getString(PREFS_KEY, null) ?: return defaultHistory()
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optString("id")
                val prompt = obj.optString("prompt")
                val invoiceNo = obj.optString("invoiceNo")
                val client = obj.optString("client")
                val amount = obj.optDouble("amount")
                val createdAt = obj.optString("createdAt")
                val invoiceJsonStr = obj.optString("invoiceJson", "")
                val invoiceData = if (invoiceJsonStr.isNotEmpty()) {
                    jsonToInvoice(JSONObject(invoiceJsonStr))
                } else {
                    InvoiceData(invoiceNo = invoiceNo, billedTo = BilledParty(name = client))
                }

                list.add(
                    InvoiceHistoryItem(
                        id = id,
                        promptDescription = prompt,
                        invoiceNo = invoiceNo,
                        clientName = client,
                        totalAmount = amount,
                        invoiceData = invoiceData,
                        createdAt = createdAt
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return if (list.isEmpty()) defaultHistory() else list
    }

    private fun invoiceToJson(data: InvoiceData): JSONObject {
        val root = JSONObject()
        root.put("invoiceNo", data.invoiceNo)
        root.put("registrationNo", data.registrationNo)
        root.put("issueDate", data.issueDate)
        root.put("dueDate", data.dueDate)
        root.put("paymentMethod", data.paymentMethod.name)
        root.put("authoriserName", data.authoriserName)
        root.put("showWatermark", data.showWatermark)

        val billed = JSONObject().apply {
            put("name", data.billedTo.name)
            put("address", data.billedTo.address)
            put("pinState", data.billedTo.pinState)
        }
        root.put("billedTo", billed)

        val from = JSONObject().apply {
            put("company", data.from.company)
            put("address", data.from.address)
            put("cityPinState", data.from.cityPinState)
        }
        root.put("from", from)

        val itemsArr = JSONArray()
        data.items.forEach { itm ->
            val itmObj = JSONObject().apply {
                put("id", itm.id)
                put("description", itm.description)
                put("amountPerHead", itm.amountPerHead)
                put("studentCount", itm.studentCount)
            }
            itemsArr.put(itmObj)
        }
        root.put("items", itemsArr)
        return root
    }

    private fun jsonToInvoice(root: JSONObject): InvoiceData {
        val invNo = root.optString("invoiceNo", "RG-JPS-2608-001")
        val regNo = root.optString("registrationNo", "273744")
        val issueDate = root.optString("issueDate", "01/09/2026")
        val dueDate = root.optString("dueDate", "08/09/2026")
        val auth = root.optString("authoriserName", "Suman Mondal")
        val watermark = root.optBoolean("showWatermark", true)
        val payMethod = try {
            PaymentMethod.valueOf(root.optString("paymentMethod", "CASH"))
        } catch (_: Exception) {
            PaymentMethod.CASH
        }

        val billedObj = root.optJSONObject("billedTo")
        val billed = BilledParty(
            name = billedObj?.optString("name", "JYOTIRMOY PUBLIC SCHOOL") ?: "JYOTIRMOY PUBLIC SCHOOL",
            address = billedObj?.optString("address", "Tematha , Sonarpur") ?: "Tematha , Sonarpur",
            pinState = billedObj?.optString("pinState", "Pin:743330 , West Bengal") ?: "Pin:743330 , West Bengal"
        )

        val fromObj = root.optJSONObject("from")
        val from = SenderParty(
            company = fromObj?.optString("company", "ROBOGYAAN") ?: "ROBOGYAAN",
            address = fromObj?.optString("address", "Dakshin Gobindopur , Sonarpur") ?: "Dakshin Gobindopur , Sonarpur",
            cityPinState = fromObj?.optString("cityPinState", "Kolkata - 700145 , West Bengal") ?: "Kolkata - 700145 , West Bengal"
        )

        val itemsList = mutableListOf<InvoiceItem>()
        val itemsArr = root.optJSONArray("items")
        if (itemsArr != null) {
            for (i in 0 until itemsArr.length()) {
                val itm = itemsArr.getJSONObject(i)
                itemsList.add(
                    InvoiceItem(
                        id = itm.optString("id", "item_$i"),
                        description = itm.optString("description", ""),
                        amountPerHead = itm.optDouble("amountPerHead", 0.0),
                        studentCount = itm.optInt("studentCount", 0)
                    )
                )
            }
        }
        if (itemsList.isEmpty()) {
            itemsList.add(InvoiceItem(description = "Robogyaan ECA Programme (Premium)", amountPerHead = 200.0, studentCount = 136))
        }

        return InvoiceData(
            invoiceNo = invNo,
            registrationNo = regNo,
            issueDate = issueDate,
            dueDate = dueDate,
            billedTo = billed,
            from = from,
            items = itemsList,
            paymentMethod = payMethod,
            authoriserName = auth,
            showWatermark = watermark
        )
    }

    private fun defaultHistory(): List<InvoiceHistoryItem> = listOf(
        InvoiceHistoryItem(
            id = "default_1",
            promptDescription = "Robogyaan ECA Program - Jyotirmoy Public School",
            invoiceNo = "RG-JPS-2608-001",
            clientName = "JYOTIRMOY PUBLIC SCHOOL",
            totalAmount = 27200.0,
            invoiceData = InvoiceData(),
            createdAt = "Recent"
        )
    )
}
