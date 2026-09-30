import { NextResponse } from 'next/server';

export const dynamic = 'force-dynamic';

export async function GET() {
  const currentVersion = '1.5.4';
  const repo = 'parthasdey2304/roboGyaanInvoice';

  try {
    const res = await fetch(`https://api.github.com/repos/${repo}/releases/latest`, {
      headers: {
        'Accept': 'application/vnd.github.v3+json',
        'User-Agent': 'RoboGyaanInvoice-Web',
      },
      next: { revalidate: 60 },
    });

    if (res.ok) {
      const data = await res.json();
      const tagName = (data.tag_name || `v${currentVersion}`).replace(/^v/, '');
      const apkAsset = data.assets?.find((a: { name?: string }) =>
        a.name?.toLowerCase().endsWith('.apk')
      );

      return NextResponse.json({
        latestVersion: tagName,
        downloadUrl:
          apkAsset?.browser_download_url ||
          `https://github.com/${repo}/releases/download/v${tagName}/robogyaan-invoice-v${tagName}.apk`,
        releaseName: data.name || `RoboGyaan Invoice Suite v${tagName}`,
        releaseNotes: data.body || '',
        publishedAt: data.published_at || '',
      });
    }
  } catch (error) {
    console.error('Failed to query GitHub releases API:', error);
  }

  // Fallback to latest known release
  return NextResponse.json({
    latestVersion: currentVersion,
    downloadUrl: `https://github.com/${repo}/releases/download/v${currentVersion}/robogyaan-invoice-v${currentVersion}.apk`,
    releaseName: `RoboGyaan Invoice Suite v${currentVersion}`,
    releaseNotes: 'Official RoboGyaan Invoice Suite APK release.',
    publishedAt: new Date().toISOString(),
  });
}
