function ensureFilesAreImages(inputForm)
{
    for (let i = 0; i < inputForm.files.length; i++)
    {
        let imageObjectToTest = new Image();
        let image = inputForm.files[i];
        imageObjectToTest.onerror = function ()
        {
            alert('There is non image file in the input');
            document.getElementById('image-label').innerHTML = 'Choose images (max is 10)';
            document.getElementById("image-label").style.color = "gray";
            inputForm.value = '';
            return;
        };
        imageObjectToTest.src = URL.createObjectURL(image);
    }
    document.getElementById('image-label').innerHTML = 'You selected ' + inputForm.files.length + ' files';
    document.getElementById("image-label").style.color = "green";
}