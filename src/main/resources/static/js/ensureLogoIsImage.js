function ensureLogoIsImage(inputField)
{
    let imageObjectToTest = new Image();
    let image = inputField.files[0];
    imageObjectToTest.onload = function ()
    {
        document.getElementById('logo-input-label').innerHTML = 'You selected: ' + image.name;
        document.getElementById("logo-input-label").style.color = "green";
    };
    imageObjectToTest.onerror = function ()
    {
        alert('You paste not an image');
        document.getElementById('logo-input-label').innerHTML = 'Choose an image file';
        document.getElementById("logo-input-label").style.color = "gray";
        inputField.value = '';
    };
    imageObjectToTest.src = URL.createObjectURL(image);
}