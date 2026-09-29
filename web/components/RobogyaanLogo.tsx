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
        style={{ transform: 'rotate(-25deg)', opacity: 0.12 }}
      >
        <svg
          viewBox="0 0 160 160"
          className="w-44 h-44 text-black fill-current"
          xmlns="http://www.w3.org/2000/svg"
        >
          {/* Robogyaan Network Nodes Emblem */}
          <line x1="80" y1="45" x2="115" y2="70" stroke="#000" strokeWidth="5" />
          <line x1="115" y1="70" x2="100" y2="110" stroke="#000" strokeWidth="5" />
          <line x1="80" y1="45" x2="55" y2="75" stroke="#000" strokeWidth="5" />
          <line x1="55" y1="75" x2="70" y2="115" stroke="#000" strokeWidth="5" />
          <line x1="70" y1="115" x2="100" y2="110" stroke="#000" strokeWidth="5" />

          {/* Connected Circular Nodes */}
          <circle cx="80" cy="45" r="14" fill="#000" />
          <circle cx="80" cy="45" r="6" fill="#FFF" />

          <circle cx="115" cy="70" r="12" fill="#000" />
          <circle cx="115" cy="70" r="5" fill="#FFF" />

          <circle cx="55" cy="75" r="13" fill="#000" />
          <circle cx="55" cy="75" r="5.5" fill="#FFF" />

          <circle cx="100" cy="110" r="11" fill="#000" />
          <circle cx="100" cy="110" r="4.5" fill="#FFF" />

          <circle cx="70" cy="115" r="10" fill="#000" />
          <circle cx="70" cy="115" r="4" fill="#FFF" />
        </svg>

        <div className="text-3xl font-black tracking-[0.25em] text-black mt-1">
          ROBOGYAAN
        </div>
        <div className="text-[10px] font-bold tracking-[0.2em] text-black uppercase mt-0.5">
          IGNITING CURIOSITY, BUILDING FUTURE
        </div>
      </div>
    );
  }

  return (
    <div className={`flex items-center gap-3 ${className}`}>
      {/* Crisp Vector / Fallback Image Logo */}
      <div className="flex flex-col items-center">
        <svg
          viewBox="0 0 100 80"
          className="w-16 h-12 text-black fill-current shrink-0"
          xmlns="http://www.w3.org/2000/svg"
        >
          {/* Network Nodes */}
          <line x1="50" y1="20" x2="72" y2="35" stroke="#000" strokeWidth="3.5" />
          <line x1="72" y1="35" x2="62" y2="60" stroke="#000" strokeWidth="3.5" />
          <line x1="50" y1="20" x2="35" y2="38" stroke="#000" strokeWidth="3.5" />
          <line x1="35" y1="38" x2="44" y2="63" stroke="#000" strokeWidth="3.5" />
          <line x1="44" y1="63" x2="62" y2="60" stroke="#000" strokeWidth="3.5" />

          <circle cx="50" cy="20" r="8" fill="#000" />
          <circle cx="50" cy="20" r="3.5" fill="#FFF" />

          <circle cx="72" cy="35" r="7" fill="#000" />
          <circle cx="72" cy="35" r="3" fill="#FFF" />

          <circle cx="35" cy="38" r="7.5" fill="#000" />
          <circle cx="35" cy="38" r="3" fill="#FFF" />

          <circle cx="62" cy="60" r="6.5" fill="#000" />
          <circle cx="62" cy="60" r="2.8" fill="#FFF" />

          <circle cx="44" cy="63" r="6" fill="#000" />
          <circle cx="44" cy="63" r="2.5" fill="#FFF" />
        </svg>

        <div className="text-xl sm:text-2xl font-black tracking-[0.18em] text-black leading-none">
          ROBOGYAAN
        </div>
        <div className="text-[7px] sm:text-[8px] font-bold tracking-[0.14em] text-neutral-600 uppercase mt-0.5 whitespace-nowrap">
          IGNITING CURIOSITY, BUILDING FUTURE
        </div>
      </div>
    </div>
  );
};
