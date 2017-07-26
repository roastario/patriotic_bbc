let sendPatrioticMessage = function () {
    return function () {
        return new Promise((resolve, reject) => {
            setTimeout(function () {
                reject("timedout");
            }, 500);
            chrome.tabs.query({active: true, currentWindow: true}, function (tabs) {
                chrome.tabs.sendMessage(tabs[0].id, {}, function (response) {
                    resolve(response.isPatriotic);
                })
            });
        });
    };
}();


function patrioticClick(event) {
    sendPatrioticMessage()
        .then(function (isPatriotic) {
            let source = event.target || event.srcElement;
            if (isPatriotic) {
                source.classList.remove('luuvieButton');
                source.classList.add('patrioticButton');
                source.innerHTML = "Enough Patriotism!"
            } else {
                source.classList.remove('patrioticButton');
                source.classList.add('luuvieButton');
                source.innerHTML = "More Patriotism!"
            }
        })
        .catch(function (rejectionReason) {
            console.log("rejected due to: " + rejectionReason);
        })
}

let button = document.getElementById("patrioticButton");
button.addEventListener("click", patrioticClick);