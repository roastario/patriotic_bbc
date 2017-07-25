/**
 * Created by stefanofranz on 25/07/17.
 */


if (document.title.indexOf("BBC News") !== -1) {
    var titleElement = document.querySelector('div.story-body > h1');

    //get a new title if available
    fetch('https://api-project-1047670303083.appspot.com/title?titleId=' + encodeURIComponent(window.location.pathname))
        .then(function (response) {
            return response.json();
        }).then(function (parsedJSON) {
        titleElement.innerHTML = parsedJSON.newTitle;
    });

    //setup editing
    titleElement.contentEditable = "true";
    titleElement.addEventListener("keydown", function (event) {
        if (event.key === 'Enter') {
            event.stopPropagation();
            titleElement.contentEditable = 'false';
            fetch('https://api-project-1047670303083.appspot.com/title', {
                'method': 'put',
                'body': JSON.stringify({
                    'titleId': window.location.pathname,
                    'newTitle': titleElement.innerHTML
                })
            }).then(function (response) {
                console.log(response.json);
                window.setTimeout(function () {
                    titleElement.contentEditable = 'true'
                }, 100);
            }).catch(function (error) {
            });

        }
    });
}