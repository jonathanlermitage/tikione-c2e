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
    document.getElementById('cssNight').disabled = !document.getElementById('cssNight').disabled;
}

document.getElementById('cssNight').disabled = true;
