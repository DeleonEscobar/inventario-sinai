import { getTokenRequest } from '/js/utils/getTokenRequest.js';

// Función para cargar los datos del dashboard
async function loadDashboardData() {
    try {
        let userToken = await getTokenRequest();
        let apiEndpoint = 'http://localhost:8081/api';
        
        if (!userToken) {
            console.error('No se pudo obtener el token');
            return;
        }

        const headers = {
            'Authorization': `Bearer ${userToken}`,
            'Content-Type': 'application/json'
        };

        // Obtener productos disponibles
        const productsResponse = await fetch(`${apiEndpoint}/dashboard/available-products`, {
            headers
        });

        const productsData = await productsResponse.json();

        document.getElementById('available-products').textContent = productsData.count;

        // Obtener movimientos pendientes
        const movementsResponse = await fetch(`${apiEndpoint}/dashboard/pending-movements`, {
            headers
        });
        const movementsData = await movementsResponse.json();
        document.getElementById('pending-movements').textContent = movementsData.count;

        // Obtener actividades recientes
        const activitiesResponse = await fetch(`${apiEndpoint}/dashboard/recent-activities`, {
            headers
        });
        const activitiesData = await activitiesResponse.json();
        
        const activitiesList = document.getElementById('activities-list');
        activitiesList.innerHTML = '';
        
        activitiesData.activities.forEach(activity => {
            const li = document.createElement('li');
            li.className = 'mb-4 pb-4 border-b border-slate-200 last:border-b-0';
            li.innerHTML = `
                <div class="flex items-start">
                    <div class="flex-grow">
                        <p class="font-semibold">${activity.description}</p>
                        <p class="text-sm text-gray-500">${new Date(activity.timestamp).toLocaleString()}</p>
                        <p class="text-xs text-gray-400">Responsable: ${activity.responsibleUser}</p>
                    </div>
                </div>
            `;
            activitiesList.appendChild(li);
        });

        // Obtener lotes próximos a vencer
        const batchesResponse = await fetch(`${apiEndpoint}/dashboard/expiring-batches`, {
            headers
        });
        const batchesData = await batchesResponse.json();
        
        const batchesList = document.getElementById('batches-expiring-soon');
        batchesList.innerHTML = '';
        
        batchesData.batches.forEach(batch => {
            const div = document.createElement('div');
            div.className = 'p-4 bg-red-50 rounded-lg';
            div.innerHTML = `
                <div class="flex justify-between items-start">
                    <div>
                        <h4 class="font-semibold">${batch.productName}</h4>
                        <p class="text-sm text-gray-600">Lote: ${batch.batchNumber}</p>
                        <p class="text-sm text-red-600">Vence: ${new Date(batch.expirationDate).toLocaleDateString()}</p>
                    </div>
                    <div class="text-right">
                        <p class="font-semibold">${batch.quantity} unidades</p>
                    </div>
                </div>
            `;
            batchesList.appendChild(div);
        });

    } catch (error) {
        console.error('Error al cargar los datos del dashboard:', error);
    }
}

// Verificar si el documento ya está listo
if (document.readyState === 'loading') {
    // Si aún no está listo, esperar al evento DOMContentLoaded
    document.addEventListener('DOMContentLoaded', () => {
        loadDashboardData();
        // Actualizar datos cada 1 minuto
        setInterval(loadDashboardData, 1 * 60 * 1000);
    });
} else {
    // Si ya está listo, ejecutar inmediatamente
    loadDashboardData();
    // Actualizar datos cada 1 minuto
    setInterval(loadDashboardData, 1 * 60 * 1000);
}