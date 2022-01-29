function concealImagesInput()
{
    if (document.getElementById('delete-previous-images').checked)
    {
        document.getElementById('images-label').style.display = 'none';
        document.getElementById('images-input-label').style.display = 'none';
    }
    else
    {
        document.getElementById('images-label').style.display = 'grid';
        document.getElementById('images-input-label').style.display = 'grid';
    }
}