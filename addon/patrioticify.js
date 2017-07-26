/**
 * Created by stefanofranz on 25/07/17.
 */

let server = "https://api-project-1047670303083.appspot.com/";

function sendUpdatedTitle(titleElement) {
    titleElement.contentEditable = 'false';
    fetch(server + 'title', {
        'method': 'put',
        'body': JSON.stringify({
            'titleId': window.location.pathname,
            'newTitle': titleElement.textContent
        })
    }).then(function (response) {
        window.setTimeout(function () {
            titleElement.contentEditable = 'true'
        }, 100);
    }).catch(function (error) {
    });
}

if (document.title.indexOf("BBC News") !== -1) {
    let titleElement = document.querySelector('div.story-body > h1');
    let isPatriotic = true;

    chrome.runtime.onMessage.addListener(function (request, sender, sendResponse) {
        if (!(isPatriotic = !isPatriotic)) {
            if (titleElement.oldTitle) {
                titleElement.innerHTML = titleElement.oldTitle;
            }
        } else {
            if (titleElement.newTitle) {
                titleElement.innerHTML = titleElement.newTitle;
            }
        }
        sendResponse({isPatriotic: isPatriotic});
    });


    //get a new title if available
    fetch(server + 'title?titleId=' + encodeURIComponent(window.location.pathname))
        .then(function (response) {
            return response.json();
        }).then(function (parsedJSON) {
        titleElement.oldTitle = titleElement.innerHTML;
        titleElement.newTitle = parsedJSON.newTitle;
        titleElement.innerHTML = titleElement.newTitle;
    });

    //setup editing

    titleElement.contentEditable = "true";

    titleElement.addEventListener("blur", function (event) {
        sendUpdatedTitle(event.target || event.srcElement);
    });

    titleElement.addEventListener("keydown", function (event) {
        if (event.key === 'Enter') {
            event.stopPropagation();
            sendUpdatedTitle(event.target || event.srcElement);
        }
    });
}

