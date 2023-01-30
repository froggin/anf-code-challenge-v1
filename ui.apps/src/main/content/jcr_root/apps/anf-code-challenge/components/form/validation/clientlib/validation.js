/* ***Begin Code - Nicholaus Chipping*** */

(function() {
    "use strict";

    var selectors = {
        form: ".cmp-form",
        submit: ".cmp-form-button[name='submit']"
    };

    function serialize(form) {
        var query = [];
        if (form && form.elements) {
            for (var i = 0; i < form.elements.length; i++) {
                var node = form.elements[i];
                if (!node.disabled && node.name) {
                    var param = [node.name, encodeURIComponent(node.value)];
                    query.push(param.join("="));
                }
            }
        }
        return query.join("&");
    }

    function FormValidation(config) {
        if (config.elements) {
            this._elements = {};
            this._elements.form = config.elements.form[0];
            this._elements.submitButton = config.elements.submitButton[0];
        }
        this._action = this._elements.form.getAttribute("action");
        this._elements.submitButton.addEventListener("click", this._validateForm.bind(this));
    }

    FormValidation.prototype._validateForm = function(e) {
        e.preventDefault();
        var request = new XMLHttpRequest();
        var url = "/bin/saveUserDetails?" + serialize(this._elements.form);
        request.open("GET", url, true);
        request.formValidation = this;

        request.onload = function() {
            if (request.status >= 200 && request.status < 400) {
                // success status
                var data = JSON.parse(request.responseText);
                var serverResponse = data.message[0];
                if (data.error && data.error[0] == true) {
                    alert(serverResponse);
                } else {
                    var successElement = document.createElement('p');
                    successElement.innerText = serverResponse;
                    this.formValidation._elements.form.parentElement.append(successElement);
                    this.formValidation._elements.form.remove();
                }
            }
        };
        request.send();
    }

    function onDocumentReady() {
        var form = document.querySelectorAll(selectors.form);
        var submitButton = document.querySelectorAll(selectors.submit);
        new FormValidation({ elements: {"form": form , "submitButton": submitButton} });
    }

    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }

})();

/* ***END Code***** */