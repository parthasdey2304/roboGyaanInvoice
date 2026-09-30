import React from 'react';

interface NeoBrutalCardProps {
  children: React.ReactNode;
  title?: string;
  badge?: string;
  variant?: 'white' | 'yellow' | 'dark' | 'orange' | 'gray';
  className?: string;
  headerAction?: React.ReactNode;
}

export const NeoBrutalCard: React.FC<NeoBrutalCardProps> = ({
  children,
  title,
  badge,
  variant = 'white',
  className = '',
  headerAction,
}) => {
  const bgColors = {
    white: 'bg-white text-black dark:bg-[#18181b] dark:text-white dark:border-neutral-700',
    yellow: 'bg-[#FFE600] text-black',
    dark: 'bg-[#1E1E1E] text-white',
    orange: 'bg-[#FFA500] text-black',
    gray: 'bg-[#F3F4F6] text-black dark:bg-[#27272a] dark:text-white',
  };

  return (
    <div
      className={`border-[2.5px] border-black rounded-lg shadow-[4px_4px_0px_0px_#000000] p-4 sm:p-5 transition-all ${bgColors[variant]} ${className}`}
    >
      {(title || badge || headerAction) && (
        <div className="flex items-start justify-between gap-2 pb-3 mb-3 border-b-2 border-black/80 dark:border-neutral-700">
          {/* Left Title: Written normally */}
          <div>
            {title && (
              <h3 className="text-base sm:text-lg font-black tracking-tight text-black dark:text-white whitespace-pre-line leading-snug">
                {title}
              </h3>
            )}
          </div>

          {/* Top Right: Badges (Recipient / Sender) & Actions sitting higher */}
          <div className="flex items-center gap-2 shrink-0 -mt-1 sm:-mt-1.5">
            {badge && (
              <span className="px-2.5 py-0.5 text-xs font-black uppercase tracking-wider bg-black text-white rounded shadow-sm">
                {badge}
              </span>
            )}
            {headerAction && <div>{headerAction}</div>}
          </div>
        </div>
      )}
      {children}
    </div>
  );
};
