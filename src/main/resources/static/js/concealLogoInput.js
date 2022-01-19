function concealLogoInput()
{
    if (document.getElementById('delete-previous-logo').checked)
    {
        document.getElementById('select-logo-block').style.display = 'none';
    }
    else
    {
        document.getElementById('select-logo-block').style.display = 'block';
    }
}