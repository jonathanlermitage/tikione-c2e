function showToc(visible) {
    document.getElementById('toc').style.visibility = (visible ? 'visible' : 'hidden');
    document.getElementById('articles').style.visibility = (visible ? 'hidden' : 'visible');
}

function burger() {
    showToc(document.getElementById('toc').style.visibility === 'hidden');
}

function showPicture(id, idBox) {
    document.getElementById(id).style.maxWidth = (document.getElementById(id).style.maxWidth !== '100%' ? '100%' : '200px');
    document.getElementById(idBox).style.maxWidth = (document.getElementById(idBox).style.maxWidth !== '100%' ? '100%' : '200px');
    document.getElementById(id).style.maxHeight = (document.getElementById(id).style.maxHeight !== '100%' ? '100%' : '150px');
}

function switchTheme() {
    var dayTheme = !document.getElementById('cssNight').disabled;
    document.getElementById('fun-pic-day').style.display = (dayTheme ? 'block' : 'none');
    document.getElementById('fun-pic-night').style.display = (dayTheme ? 'none' : 'block');
    document.getElementById('cssNight').disabled = dayTheme;
}

document.getElementById('cssNight').disabled = true;
