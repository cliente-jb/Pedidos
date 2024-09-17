// Función para cargar los pedidos desde el servidor
async function cargarPedidos() {
    try {
        const response = await fetch('/api/pedidos');
        const pedidos = await response.json();
        const pedidosContainer = document.getElementById('pedidos');

        pedidosContainer.innerHTML = '';

        pedidos.forEach(pedido => {
            const pedidoDiv = document.createElement('div');
            pedidoDiv.className = 'pedido';
            pedidoDiv.innerHTML = `
                <img src="${pedido.imagen}" alt="Imagen del pedido">
                <p><strong>Cliente:</strong> ${pedido.nombreCliente}</p>
                <p><strong>Descripción:</strong> ${pedido.descripcion}</p>
                <p><strong>Fecha y Hora:</strong> ${pedido.fechaHora}</p>
                <p><strong>Tamaño:</strong> ${pedido.Libra}</p>
                <p><strong>Relleno:</strong> ${pedido.ingredientes.join(', ')}</p>
                <p><strong>Base:</strong> ${pedido.base.join(', ')}</p>
                <p><strong>Imprimir:</strong> ${pedido.imprimir.join(', ')}</p>
                <p><strong>Dedicatoria:</strong> ${pedido.dedicatoria}</p>
                <p><strong>Dirección de Entrega:</strong> ${pedido.direccion || 'N/A'}</p>
                <button onclick="actualizarEstado('${pedido.id}')">Marcar como Preparado</button>
            `;
            pedidosContainer.appendChild(pedidoDiv);
        });
    } catch (error) {
        console.error('Error al cargar pedidos:', error);
    }
}

// Función para actualizar el estado de un pedido
async function actualizarEstado(id) {
    try {
        const response = await fetch(`/api/pedido/${id}/actualizar`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ estado: 'preparado' })
        });

        const result = await response.json();

        if (result.success) {
            alert('Estado del pedido actualizado a Preparado.');
            cargarPedidos(); // Actualiza la lista de pedidos después de cambiar el estado
        } else {
            alert('Error al actualizar el estado del pedido.');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al actualizar el estado del pedido.');
    }
}

// Cargar los pedidos al iniciar la página
cargarPedidos();
