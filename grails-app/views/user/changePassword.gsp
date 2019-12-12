<%--
  Created by IntelliJ IDEA.
  User: root
  Date: 7/1/14
  Time: 3:12 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Change Password</title>
    <script language="javascript" src="${resource(dir: 'form-validator', file: 'security.js')}"></script>
</head>

<body>
<div id="content" role="main">
    <div class="small-container">
        <h1>Change Password</h1>
        <g:if test="${flash.validationError}">
            <div class="error">${flash.validationError}</div>
        </g:if>
        <g:form action="saveNewPassword" name="changePasswordForm">
            <g:if test="${askForOldPassword}">
                <p>
                    <label for="oldPassword">Old Password</label>
                    <input type="password" class="k-textbox" name="oldPassword" id="oldPassword"/>
                </p>
            </g:if>
            <p>
                <label for="newPassword_confirmation">New Password</label>
                <input type="password" class="k-textbox" name="newPassword_confirmation" id="newPassword_confirmation"/>
            </p>

            <p>
                <label for="newPassword">Confirm New Password</label>
                <input type="password" class="k-textbox" name="newPassword" id="newPassword"/>
            </p>

            <p>
                <input type="submit" id="submit" class="k-button" value="Save New Password"/>
            </p>
        </g:form>
    </div>
</div>
</body>
</html>