function concealImagesInput()
{
    if (document.getElementById('delete-previous-images').checked)
    {
        document.getElementById('select-images-block').style.display = 'none';
    }
    else
    {
        document.getElementById('select-images-block').style.display = 'block';
    }
}