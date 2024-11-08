tinymce.init({
    selector: '#descripcion',
    plugins: 'advlist autolink lists link image charmap print preview hr anchor pagebreak',
    toolbar_mode: 'floating',
    branding: false,
    menubar: false,
    setup: function (editor) {
        editor.on('change', function () {
            editor.save();
        });
    }
});

const obtenerFechaActual = () => {
    const hoy = new Date();
    const dia = String(hoy.getDate()).padStart(2, '0');
    const mes = String(hoy.getMonth() + 1).padStart(2, '0'); // Los meses empiezan desde 0
    const anio = hoy.getFullYear();
    return `${anio}-${mes}-${dia}`;
};

document.addEventListener('DOMContentLoaded', (event) => {
    document.getElementById('fechaCreacion').value = obtenerFechaActual();
});