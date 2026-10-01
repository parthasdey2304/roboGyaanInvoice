import React from 'react';

interface RobogyaanLogoProps {
  className?: string;
  isWatermark?: boolean;
}

export const RobogyaanLogo: React.FC<RobogyaanLogoProps> = ({
  className = '',
  isWatermark = false,
}) => {
  if (isWatermark) {
    return (
      <div
        className={`pointer-events-none select-none flex flex-col items-center justify-center ${className}`}
        style={{
          transform: 'rotate(-20deg)',
          opacity: 0.10,
        }}
      >
        {/* Genuine RoboGyaan Symbol Watermark */}
        <img
          src="/robogyaan_symbol.png"
          alt="RoboGyaan Symbol Watermark"
          className="w-32 h-32 sm:w-40 sm:h-40 object-contain"
        />
      </div>
    );
  }

  return (
    <div className={`flex items-center ${className}`}>
      {/* Official Authentic RoboGyaan Logo */}
      <img
        src="/robogyaan_logo.png"
        alt="RoboGyaan Logo"
        className="h-12 sm:h-14 w-auto object-contain shrink-0"
      />
    </div>
  );
};
