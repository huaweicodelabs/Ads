/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

function Toast() {

}

Toast.prototype.show = function(msg) {
    let toastHtml = `
        <span class='toast-content'>${msg}</span>
    `

   let toastElment = document.createElement('div')
   toastElment.setAttribute('class', 'toast-container')
    document.body.append(toastElment);
    toastElment.innerHTML = toastHtml;
    setTimeout(()=>{
        toastElment.parentNode.removeChild(toastElment);
    }, 2000)
}

window.$Toast = new Toast();


function addEvent(elements, event, func) {
    if (isElement(elements)) {
        elements.addEventListener(event, func, false);
        return;
    }
    if (!elements || elements.length == 0) {
        return;
    }

    for (i = 0; i < elements.length; i++) {
        elements[i].addEventListener(event, func, false);
    }
}

function isElement(element) {
    if(element && element.nodeType == 1) {
        return true;
    }

    return false;
}

function isArray(obj) {
    return Object.prototype.toString.call(obj) === '[object Array]';
}