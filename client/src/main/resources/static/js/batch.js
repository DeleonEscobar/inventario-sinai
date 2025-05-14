import { getTokenRequest } from '/js/utils/getTokenRequest.js';
import { formatDate } from '/js/utils/formatDate.js';

const apiEndpoint = 'http://localhost:8081/api/batches';
const tableBody = document.getElementById('batches-table-body');
const btnAddBatch = document.getElementById('btn-add-batch');
const modal = document.getElementById('batch-modal');
const closeModalBtn = document.getElementById('close-modal');
const cancelModalBtn = document.getElementById('cancel-modal');
const modalTitle = document.getElementById('modal-title');
const batchForm = document.getElementById('batch-form');

const inputId = document.getElementById('batch-id');
const inputSerial = document.getElementById('batch-serial');
const inputProduct = document.getElementById('batch-product');
const inputAmount = document.getElementById('batch-amount');
const inputPrice = document.getElementById('batch-price');
const inputExpiration = document.getElementById('batch-expiration');

let editing = false;
let productsList = [];

async function fetchProductsForSelect() {
    try {
        let userToken = await getTokenRequest();
        if (!userToken) return [];
        const headers = {
            'Authorization': `Bearer ${userToken}`,
            'Content-Type': 'application/json'
        };
        const res = await fetch('http://localhost:8081/api/products', { headers });
        if (!res.ok) throw new Error('Error al obtener productos');
        return await res.json();
    } catch (err) {
        console.error('Error al cargar productos para lotes:', err);
        return [];
    }
}

async function fillProductSelect(selectedId = null) {
    productsList = await fetchProductsForSelect();
    inputProduct.innerHTML = '<option value="">Seleccione un producto</option>';
    productsList.forEach(p => {
        const option = document.createElement('option');
        option.value = p.id;
        option.textContent = p.name;
        if (selectedId && p.id == selectedId) option.selected = true;
        inputProduct.appendChild(option);
    });
}

function openModal(edit = false, batch = null) {
    modal.classList.remove('hidden');
    editing = edit;
    if (edit && batch) {
        modalTitle.textContent = 'Editar Lote';
        inputId.value = batch.id;
        inputSerial.value = batch.serialNumber;
        fillProductSelect(batch.product ? batch.product.id : null);
        inputAmount.value = batch.amount;
        inputPrice.value = batch.price;
        inputExpiration.value = batch.expirationDate ? batch.expirationDate.split('T')[0] : '';
    } else {
        modalTitle.textContent = 'Agregar Lote';
        batchForm.reset();
        inputId.value = '';
        fillProductSelect();
    }
}

function closeModal() {
    modal.classList.add('hidden');
    batchForm.reset();
    inputId.value = '';
}

btnAddBatch.addEventListener('click', () => openModal(false));
closeModalBtn.addEventListener('click', closeModal);
cancelModalBtn.addEventListener('click', closeModal);
window.addEventListener('click', (e) => {
    if (e.target === modal) closeModal();
});

async function fetchBatches() {
    try {
        tableBody.innerHTML = '<tr><td colspan="9" class="text-center py-6 text-slate-400">Cargando...</td></tr>';
        let userToken = await getTokenRequest();
        if (!userToken) {
            console.error('No se pudo obtener el token');
            tableBody.innerHTML = '<tr><td colspan="9" class="text-center py-6 text-red-500">Error de autenticación</td></tr>';
            return;
        }
        const headers = {
            'Authorization': `Bearer ${userToken}`,
            'Content-Type': 'application/json'
        };
        const res = await fetch(apiEndpoint, { headers });
        if (!res.ok) throw new Error(`Error HTTP: ${res.status}`);
        const batches = await res.json();
        renderBatches(batches);
    } catch (err) {
        console.error('Error al cargar lotes:', err);
        tableBody.innerHTML = '<tr><td colspan="9" class="text-center py-6 text-red-500">Error al cargar lotes</td></tr>';
    }
}

function renderBatches(batches) {
    if (!batches || !batches.length) {
        tableBody.innerHTML = '<tr><td colspan="9" class="text-center py-6 text-slate-400">No hay lotes registrados</td></tr>';
        return;
    }
    tableBody.innerHTML = '';
    batches.forEach(batch => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="px-6 py-4">${batch.serialNumber}</td>
            <td class="px-6 py-4">${batch.product ? batch.product.name : ''}</td>
            <td class="px-6 py-4">${batch.amount}</td>
            <td class="px-6 py-4">$${batch.price?.toFixed(2) ?? ''}</td>
            <td class="px-6 py-4">${formatDate(batch.expirationDate)}</td>
            <td class="px-6 py-4">${formatDate(batch.createdAt)}</td>
            <td class="px-6 py-4">${formatDate(batch.updatedAt)}</td>
            <td class="px-6 py-4 text-center">
                <button class="edit-btn text-green-600 hover:underline mr-2" data-id="${batch.id}">Editar</button>
                <button class="delete-btn text-red-600 hover:underline" data-id="${batch.id}">Eliminar</button>
                <button class="goto-product-btn text-blue-600 hover:underline ml-2" data-product-id="${batch.product ? batch.product.id : ''}">Editar Producto</button>
            </td>
        `;
        tableBody.appendChild(tr);
    });
    assignButtonEvents();
}

async function assignButtonEvents() {
    document.querySelectorAll('.edit-btn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            try {
                const id = e.target.getAttribute('data-id');
                let userToken = await getTokenRequest();
                if (!userToken) {
                    console.error('No se pudo obtener el token');
                    alert('Error de autenticación');
                    return;
                }
                const headers = {
                    'Authorization': `Bearer ${userToken}`,
                    'Content-Type': 'application/json'
                };
                const res = await fetch(`${apiEndpoint}/${id}`, { headers });
                if (!res.ok) throw new Error(`Error HTTP: ${res.status}`);
                const batch = await res.json();
                openModal(true, batch);
            } catch (err) {
                console.error('Error al obtener detalles del lote:', err);
                alert('No se pudo cargar la información del lote');
            }
        });
    });
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            try {
                const id = e.target.getAttribute('data-id');
                if (confirm('¿Seguro que deseas eliminar este lote?')) {
                    let userToken = await getTokenRequest();
                    if (!userToken) {
                        console.error('No se pudo obtener el token');
                        alert('Error de autenticación');
                        return;
                    }
                    const headers = {
                        'Authorization': `Bearer ${userToken}`,
                        'Content-Type': 'application/json'
                    };
                    const res = await fetch(`${apiEndpoint}/${id}`, {
                        method: 'DELETE',
                        headers
                    });
                    if (!res.ok) throw new Error(`Error HTTP: ${res.status}`);
                    fetchBatches();
                }
            } catch (err) {
                console.error('Error al eliminar el lote:', err);
                alert('No se pudo eliminar el lote');
            }
        });
    });
    document.querySelectorAll('.goto-product-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const productId = e.target.getAttribute('data-product-id');
            if (productId) {
                if (confirm('Vas a ser redirigido a la edición del producto. Si tienes cambios sin guardar en lotes, se perderán. ¿Continuar?')) {
                    window.location.href = `/dashboard/admin/products?editProductId=${productId}`;
                }
            }
        });
    });
}

batchForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        const id = inputId.value;
        const productId = parseInt(inputProduct.value, 10);
        if (!productId) {
            alert('Debe seleccionar un producto válido.');
            return;
        }
        let expirationDate = inputExpiration.value;
        if (expirationDate && expirationDate.length === 10) {
            expirationDate = expirationDate + 'T00:00:00Z';
        }
        const batch = {
            serialNumber: inputSerial.value,
            product: { id: productId },
            amount: parseInt(inputAmount.value, 10),
            price: parseFloat(inputPrice.value),
            expirationDate
        };
        if (isNaN(batch.amount) || isNaN(batch.price)) {
            alert('Cantidad y precio deben ser valores numéricos válidos.');
            return;
        }
        let userToken = await getTokenRequest();
        if (!userToken) {
            console.error('No se pudo obtener el token');
            alert('Error de autenticación');
            return;
        }
        const headers = {
            'Authorization': `Bearer ${userToken}`,
            'Content-Type': 'application/json'
        };
        let res;
        console.log(batch);
        if (editing && id) {
            res = await fetch(`${apiEndpoint}/${id}`, {
                method: 'PUT',
                headers,
                body: JSON.stringify(batch)
            });
        } else {
            res = await fetch(apiEndpoint, {
                method: 'POST',
                headers,
                body: JSON.stringify(batch)
            });
        }
        if (!res.ok) throw new Error(`Error HTTP: ${res.status}`);
        closeModal();
        fetchBatches();
    } catch (err) {
        console.log(err);
        console.error('Error al guardar el lote:', err);
        alert('Error al guardar el lote');
    }
});

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        fetchBatches();
    });
} else {
    fetchBatches();
} 