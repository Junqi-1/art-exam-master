const loginButton = document.querySelector('.submit');
loginButton.addEventListener('click', function() {
    var errorDiv = document.getElementById("error-msg");
    errorDiv.innerHTML = "";

    var userName = document.getElementsByName("userName")[0].value;
    var pwd = document.getElementsByName("pwd")[0].value;

    if (!userName) {
        errorDiv.innerHTML += "请输入用户名。";
        return;
    }

    if (!pwd) {
        errorDiv.innerHTML += "<br>请输入密码。";
        return;
    }

    var user = {
        userName: userName,
        pwd: pwd
    }

    fetch(
        '/api/member/loginwithusername',
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
        return response.json();
    })
    .then(function(result) {
        console.log(result);
        if (result.isSuccess === true) {
            if (result.code == '505') {
                window.location.href = "/changepw.html";
            } else {
                window.location.href = "/index.html";
            }
        }else {
            errorDiv.innerHTML += "登录失败";
            if (result.message) {
                errorDiv.innerHTML += "<br>" + result.message;
            }
        }
    });

});