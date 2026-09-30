'use client';

import React, { useEffect, useRef } from 'react';

interface InteractiveGridBackgroundProps {
  theme?: 'light' | 'dark';
  enabled?: boolean;
}

export const InteractiveGridBackground: React.FC<InteractiveGridBackgroundProps> = ({
  theme = 'light',
  enabled = true,
}) => {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const mouseRef = useRef<{ x: number; y: number; active: boolean; targetX: number; targetY: number }>({
    x: -1000,
    y: -1000,
    targetX: -1000,
    targetY: -1000,
    active: false,
  });

  useEffect(() => {
    if (!enabled) return;

    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    let animationFrameId: number;
    const cellSize = 42; // Size of each square box
    const swellRadius = 180; // Radius around the cursor that swells
    const maxSwell = 18; // Max displacement in pixels

    const handleResize = () => {
      const dpr = window.devicePixelRatio || 1;
      const w = window.innerWidth;
      const h = window.innerHeight;
      canvas.width = Math.floor(w * dpr);
      canvas.height = Math.floor(h * dpr);
      canvas.style.width = `${w}px`;
      canvas.style.height = `${h}px`;
      ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    };

    handleResize();
    window.addEventListener('resize', handleResize);

    const handleMouseMove = (e: MouseEvent) => {
      mouseRef.current.targetX = e.clientX;
      mouseRef.current.targetY = e.clientY;
      mouseRef.current.active = true;
    };

    const handleMouseLeave = () => {
      mouseRef.current.active = false;
      mouseRef.current.targetX = -1000;
      mouseRef.current.targetY = -1000;
    };

    window.addEventListener('mousemove', handleMouseMove);
    window.addEventListener('mouseleave', handleMouseLeave);

    const render = () => {
      // Smoothly interpolate mouse position (lerp)
      const mouse = mouseRef.current;
      mouse.x += (mouse.targetX - mouse.x) * 0.15;
      mouse.y += (mouse.targetY - mouse.y) * 0.15;

      const width = window.innerWidth;
      const height = window.innerHeight;

      ctx.clearRect(0, 0, width, height);

      const isDark = theme === 'dark';

      // Base background color - soft elevated slate-charcoal with a touch of white for comfortable visibility
      ctx.fillStyle = isDark ? '#22242e' : '#FDFBF7';
      ctx.fillRect(0, 0, width, height);

      // Grid line and swell colors - crisp, clearly visible cross-cross box-box lines
      const gridLineColor = isDark
        ? 'rgba(255, 255, 255, 0.22)'
        : 'rgba(0, 0, 0, 0.14)';
      const swellAuraColor = isDark
        ? 'rgba(255, 230, 0, 0.18)'
        : 'rgba(255, 230, 0, 0.22)';
      const activeLineColor = isDark
        ? 'rgba(255, 255, 255, 0.45)'
        : 'rgba(0, 0, 0, 0.35)';

      // 1. Draw tactile radial glow under cursor when active
      if (mouse.x > -500 && mouse.y > -500) {
        const gradient = ctx.createRadialGradient(
          mouse.x,
          mouse.y,
          0,
          mouse.x,
          mouse.y,
          swellRadius
        );
        gradient.addColorStop(0, swellAuraColor);
        gradient.addColorStop(0.6, swellAuraColor);
        gradient.addColorStop(1, 'rgba(0, 0, 0, 0)');
        ctx.fillStyle = gradient;
        ctx.beginPath();
        ctx.arc(mouse.x, mouse.y, swellRadius, 0, Math.PI * 2);
        ctx.fill();
      }

      // Compute grid dimensions (40px square grid boxes)
      const cols = Math.ceil(width / cellSize) + 2;
      const rows = Math.ceil(height / cellSize) + 2;

      // 2. Pre-calculate displaced grid vertices
      const points: { x: number; y: number }[][] = [];

      for (let r = 0; r <= rows; r++) {
        points[r] = [];
        const basePy = (r - 1) * cellSize;

        for (let c = 0; c <= cols; c++) {
          const basePx = (c - 1) * cellSize;

          let px = basePx;
          let py = basePy;

          if (mouse.x > -500) {
            const dx = basePx - mouse.x;
            const dy = basePy - mouse.y;
            const dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < swellRadius && dist > 0.001) {
              // Smooth cosine bell-curve for 3D lens swelling
              const factor = Math.cos((dist / swellRadius) * (Math.PI / 2));
              const displacement = factor * factor * maxSwell;
              const angle = Math.atan2(dy, dx);

              px = basePx + Math.cos(angle) * displacement;
              py = basePy + Math.sin(angle) * displacement;
            }
          }

          points[r][c] = { x: px, y: py };
        }
      }

      // 3. Render horizontal grid lines connecting vertices
      ctx.lineWidth = 1;
      for (let r = 0; r <= rows; r++) {
        ctx.beginPath();
        ctx.strokeStyle = gridLineColor;
        for (let c = 0; c <= cols; c++) {
          const pt = points[r][c];
          if (c === 0) {
            ctx.moveTo(pt.x, pt.y);
          } else {
            ctx.lineTo(pt.x, pt.y);
          }
        }
        ctx.stroke();
      }

      // 4. Render vertical grid lines connecting vertices
      for (let c = 0; c <= cols; c++) {
        ctx.beginPath();
        ctx.strokeStyle = gridLineColor;
        for (let r = 0; r <= rows; r++) {
          const pt = points[r][c];
          if (r === 0) {
            ctx.moveTo(pt.x, pt.y);
          } else {
            ctx.lineTo(pt.x, pt.y);
          }
        }
        ctx.stroke();
      }

      // 5. Draw prominent intersection cross points near cursor for 3D tactile feedback
      if (mouse.x > -500) {
        for (let r = 0; r <= rows; r++) {
          for (let c = 0; c <= cols; c++) {
            const pt = points[r][c];
            const dist = Math.hypot(pt.x - mouse.x, pt.y - mouse.y);
            if (dist < swellRadius) {
              const nodeAlpha = Math.max(0, 1 - dist / swellRadius);
              ctx.fillStyle = isDark
                ? `rgba(255, 230, 0, ${0.65 * nodeAlpha})`
                : `rgba(0, 0, 0, ${0.45 * nodeAlpha})`;
              ctx.beginPath();
              ctx.arc(pt.x, pt.y, 2, 0, Math.PI * 2);
              ctx.fill();
            }
          }
        }
      }

      animationFrameId = requestAnimationFrame(render);
    };

    render();

    return () => {
      window.removeEventListener('resize', handleResize);
      window.removeEventListener('mousemove', handleMouseMove);
      window.removeEventListener('mouseleave', handleMouseLeave);
      cancelAnimationFrame(animationFrameId);
    };
  }, [theme, enabled]);

  if (!enabled) {
    return (
      <div
        className={`fixed inset-0 pointer-events-none z-0 transition-colors duration-200 ${
          theme === 'dark' ? 'bg-[#22242e]' : 'bg-[#FDFBF7]'
        }`}
      />
    );
  }

  return (
    <canvas
      ref={canvasRef}
      className="fixed inset-0 pointer-events-none z-0 block w-full h-full"
    />
  );
};
