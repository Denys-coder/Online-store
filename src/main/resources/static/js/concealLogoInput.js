function concealLogoInput()
{
    if (document.getElementById('delete-previous-logo').checked)
    {
        document.getElementById('logo-label').style.display = 'none';
        document.getElementById('logo-input-label').style.display = 'none';
    }
    else
    {
        document.getElementById('logo-label').style.display = 'grid';
        document.getElementById('logo-input-label').style.display = 'grid';
    }
}