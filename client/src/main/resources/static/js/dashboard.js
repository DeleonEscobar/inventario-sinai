import { getTokenRequest } from '/js/utils/getTokenRequest.js';

$(document).ready(function() {
    loadDashboardData();
    // Actualizar datos cada 1 minuto
    setInterval(loadDashboardData, 1 * 60 * 1000);
});

async function loadDashboardData() {
    try {
        const userToken = await getTokenRequest();
        if (!userToken) {
            console.error('No se pudo obtener el token');
            return;
        }
        
        const apiEndpoint = 'http://localhost:8081/api';
        const headers = { 'Authorization': `Bearer ${userToken}` };
        
        // Obtener todos los datos en paralelo para mayor eficiencia
        const [productsData, movementsData, activitiesData, batchesData] = await Promise.all([
            $.ajax({
                url: `${apiEndpoint}/dashboard/available-products`,
                headers
            }),
            $.ajax({
                url: `${apiEndpoint}/dashboard/pending-movements`,
                headers
            }),
            $.ajax({
                url: `${apiEndpoint}/dashboard/recent-activities`,
                headers
            }),
            $.ajax({
                url: `${apiEndpoint}/dashboard/expiring-batches`,
                headers
            })
        ]);
        
        // Actualizar contadores
        $('#available-products').text(productsData.count);
        $('#pending-movements').text(movementsData.count);
        
        // Actualizar actividades recientes
        const $activitiesList = $('#activities-list').empty();
        
        $.each(activitiesData.activities, function(i, activity) {
            $('<li>')
                .addClass('mb-4 pb-4 border-b border-slate-200 last:border-b-0')
                .html(`
                    <div class="flex items-start">
                        <div class="flex-grow">
                            <p class="font-semibold">${activity.description}</p>
                            <p class="text-sm text-gray-500">${new Date(activity.timestamp).toLocaleString()}</p>
                            <p class="text-xs text-gray-400">Responsable: ${activity.responsibleUser}</p>
                        </div>
                    </div>
                `)
                .appendTo($activitiesList);
        });
        
        // Actualizar lotes próximos a vencer
        const $batchesList = $('#batches-expiring-soon').empty();
        
        $.each(batchesData.batches, function(i, batch) {
            $('<div>')
                .addClass('p-4 bg-red-50 rounded-lg')
                .html(`
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
                `)
                .appendTo($batchesList);
        });
        
    } catch (error) {
        console.error('Error al cargar los datos del dashboard:', error);
    }
}