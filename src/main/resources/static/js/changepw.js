const loginButton = document.querySelector('.submit');
loginButton.addEventListener('click', function() {
    var errorDiv = document.getElementById("error-msg");
    errorDiv.innerHTML = "";

    var pwds = document.getElementsByName("pwd");

    if (!pwds[0].value || !pwds[1].value || pwds[0].value != pwds[1].value) {
        errorDiv.innerHTML += "请输入两次相同密码";
        return;
    }

    var user = {
        pwd: pwds[0].value
    }

    fetch(
        '/api/member/changepwd',
        {
            body: JSON.stringify(user),
            cache: 'no-cache',
            headers: {
                'content-type': 'application/json'
            },
            method: 'POST'
        }
    )
    .then(function(response) {
        if (response.status == 401) {
            return response.text();
        } else {
            return response.json();
        }
    })
    .then(function(result) {
        console.log(result);
        if (!(result instanceof Object)) {
            window.location.href = result;
        } else if (result.isSuccess === true) {
            window.location.href = "/index.html";
        } else {
            errorDiv.innerHTML += "修改密码失败";
            if (result.message) {
                errorDiv.innerHTML += "<br>" + result.message;
            }
        }
    });

});