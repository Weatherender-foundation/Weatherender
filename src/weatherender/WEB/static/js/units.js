document.addEventListener('DOMContentLoaded', function () {
    let isMetric = true;
    const toggleBtn = document.getElementById('unit-toggle');
    const mainCard = document.getElementById('main-card');
    if (!toggleBtn) return;

    function render() {
        document.querySelectorAll('.temp-value').forEach(el => {
            const c = parseFloat(el.dataset.c);
            if (Number.isNaN(c)) return;
            el.textContent = isMetric
                ? `${c}°C`
                : `${Math.round((c * 9 / 5 + 32) * 10) / 10}°F`;
        });

        document.querySelectorAll('.speed-value').forEach(el => {
            const kmh = parseFloat(el.dataset.kmh);
            if (Number.isNaN(kmh)) return;
            el.textContent = isMetric
                ? `${kmh} km/h`
                : `${Math.round(kmh * 0.621371 * 10) / 10} mph`;
        });

        toggleBtn.textContent = isMetric
            ? '°C / °F | km/h / mph'
            : '°F / °C | mph / km/h';
    }

    toggleBtn.addEventListener('click', function () {
        isMetric = !isMetric;
        render();
    });

    if (mainCard) {
        const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
        if (!prefersReducedMotion) {
            window.addEventListener('pointermove', function (event) {
                const x = (event.clientX / window.innerWidth - 0.5) * 12;
                const y = (event.clientY / window.innerHeight - 0.5) * 12;
                mainCard.style.transform = `perspective(1200px) rotateX(${(-y).toFixed(2)}deg) rotateY(${x.toFixed(2)}deg) translateY(-2px)`;
                mainCard.style.boxShadow = `0 30px 80px rgba(9, 19, 31, 0.22), ${(-x * 1.8).toFixed(2)}px ${(-y * 1.5).toFixed(2)}px 24px rgba(75, 124, 255, 0.18)`;
            });

            window.addEventListener('pointerleave', function () {
                mainCard.style.transform = 'perspective(1200px) rotateX(0deg) rotateY(0deg)';
                mainCard.style.boxShadow = '0 24px 70px rgba(9, 19, 31, 0.18)';
            });
        }
    }

    render();
});
