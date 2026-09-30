import React from 'react';

interface NeoBrutalButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'yellow' | 'white' | 'dark' | 'orange' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  icon?: React.ReactNode;
  children: React.ReactNode;
}

export const NeoBrutalButton: React.FC<NeoBrutalButtonProps> = ({
  variant = 'yellow',
  size = 'md',
  icon,
  children,
  className = '',
  disabled,
  ...props
}) => {
  const variantStyles = {
    yellow: 'bg-[#FFE600] hover:bg-[#FFDD00] text-black border-black',
    white: 'bg-white hover:bg-neutral-100 text-black border-black dark:bg-[#27272a] dark:text-white dark:border-neutral-600 dark:hover:bg-[#323238]',
    dark: 'bg-[#1E1E1E] hover:bg-black text-white border-black',
    orange: 'bg-[#FFA500] hover:bg-[#FF9100] text-black border-black',
    danger: 'bg-[#FF4D4D] hover:bg-[#E03A3A] text-white border-black',
  };

  const sizeStyles = {
    sm: 'text-xs px-3 py-1.5 gap-1.5 shadow-[2px_2px_0px_0px_#000000]',
    md: 'text-sm px-4 py-2 gap-2 shadow-[3px_3px_0px_0px_#000000]',
    lg: 'text-base px-6 py-3 gap-2.5 shadow-[4px_4px_0px_0px_#000000]',
  };

  return (
    <button
      disabled={disabled}
      className={`inline-flex items-center justify-center font-bold tracking-tight rounded-lg border-2 select-none transition-all duration-100 ${
        disabled
          ? 'opacity-50 cursor-not-allowed bg-neutral-200 border-neutral-400 text-neutral-500 shadow-none'
          : 'active:translate-x-[2px] active:translate-y-[2px] active:shadow-none cursor-pointer'
      } ${variantStyles[variant]} ${sizeStyles[size]} ${className}`}
      {...props}
    >
      {icon && <span className="shrink-0">{icon}</span>}
      <span>{children}</span>
    </button>
  );
};
