document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('pedidoForm');
    const confirmacionDiv = document.getElementById('confirmacion');
    const pedidosEnviadosDiv = document.getElementById('pedidosEnviados');

    form.addEventListener('submit', async (event) => {
        event.preventDefault(); // Evita que el formulario se envíe de la manera tradicional

        const formData = new FormData(form);

        try {
            // Subir la imagen a Firebase Storage
            const file = formData.get('imagen');
            const storageRef = firebase.storage().ref(`images/${file.name}`);
            await storageRef.put(file);
            const imageUrl = await storageRef.getDownloadURL();

            // Obtener los datos del formulario
            const pedido = {
                nombreCliente: formData.get('nombreCliente'),
                Telefono: formData.get('Telefono'),
                imagen: imageUrl,
                descripcion: formData.get('descripcion'),
                fechaHora: formData.get('fechaHora'),
                Libra: formData.get('Libra'),
                otherSize: formData.get('otherSize') || '',
                ingredientes: formData.getAll('ingredientes'),
                base: formData.getAll('base'),
                imprimir: formData.getAll('imprimir'),
                dedicatoria: formData.get('dedicatoria'),
                delivery: formData.has('delivery'),
                direccion: formData.get('direccion') || ''
            };

            // Guardar el pedido en Firestore
            const db = firebase.firestore();
            await db.collection('pedidos').add(pedido);

            // Mostrar confirmación
            confirmacionDiv.style.display = 'block';

            // Redirigir a la página de cocina
            window.location.href = 'https://my-pedidos.vercel.app/cocina.html';

        } catch (error) {
            console.error('Error al enviar el pedido:', error);
            alert('Hubo un problema al enviar el pedido. Inténtalo nuevamente.');
        }
    });

    const actualizarPedidosEnviados = async () => {
        const db = firebase.firestore();
        const pedidosSnapshot = await db.collection('pedidos').get();
        pedidosEnviadosDiv.innerHTML = '';

        pedidosSnapshot.forEach(doc => {
            const pedido = doc.data();
            const pedidoDiv = document.createElement('div');
            pedidoDiv.className = 'pedido';
            pedidoDiv.innerHTML = `
                <img src="${pedido.imagen}" alt="Imagen del pedido">
                <p><strong>Nombre:</strong> ${pedido.nombreCliente}</p>
                <p><strong>Teléfono:</strong> ${pedido.Telefono}</p>
                <p><strong>Descripción:</strong> ${pedido.descripcion}</p>
                <p><strong>Fecha y Hora:</strong> ${pedido.fechaHora}</p>
                <p><strong>Tamaño:</strong> ${pedido.Libra} ${pedido.otherSize ? `(${pedido.otherSize})` : ''}</p>
                <p><strong>Relleno:</strong> ${pedido.ingredientes.join(', ')}</p>
                <p><strong>Base:</strong> ${pedido.base.join(', ')}</p>
                <p><strong>Imprimir:</strong> ${pedido.imprimir.join(', ')}</p>
                <p><strong>Dedicatoria:</strong> ${pedido.dedicatoria}</p>
                <p><strong>Dirección:</strong> ${pedido.direccion || 'N/A'}</p>
            `;
            pedidosEnviadosDiv.appendChild(pedidoDiv);
        });
    };

    // Actualizar la lista de pedidos cuando la página se carga
    actualizarPedidosEnviados();
});

