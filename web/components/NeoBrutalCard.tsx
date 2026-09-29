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
    white: 'bg-white text-black',
    yellow: 'bg-[#FFE600] text-black',
    dark: 'bg-[#1E1E1E] text-white',
    orange: 'bg-[#FFA500] text-black',
    gray: 'bg-[#F3F4F6] text-black',
  };

  return (
    <div
      className={`border-[2.5px] border-black rounded-lg shadow-[4px_4px_0px_0px_#000000] p-4 sm:p-5 transition-all ${bgColors[variant]} ${className}`}
    >
      {(title || badge || headerAction) && (
        <div className="flex items-center justify-between gap-2 pb-3 mb-3 border-b-2 border-black/80">
          <div className="flex items-center gap-2">
            {badge && (
              <span className="px-2 py-0.5 text-xs font-black uppercase tracking-wider bg-black text-white rounded">
                {badge}
              </span>
            )}
            {title && <h3 className="text-base sm:text-lg font-bold tracking-tight">{title}</h3>}
          </div>
          {headerAction && <div>{headerAction}</div>}
        </div>
      )}
      {children}
    </div>
  );
};
