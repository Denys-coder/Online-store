function ensureFilesAreImages(inputForm)
{
    for (let i = 0; i < inputForm.files.length; i++)
    {
        let imageObjectToTest = new Image();
        let image = inputForm.files[i];
        imageObjectToTest.onerror = function ()
        {
            alert('There is non image file in the input');
            document.getElementById('images-input-label').innerHTML = 'Choose images';
            document.getElementById("images-input-label").style.color = "gray";
            inputForm.value = '';
            return;
        };
        imageObjectToTest.src = URL.createObjectURL(image);
    }
    document.getElementById('images-input-label').innerHTML = 'You selected ' + inputForm.files.length + ' files';
    document.getElementById("images-input-label").style.color = "green";
}