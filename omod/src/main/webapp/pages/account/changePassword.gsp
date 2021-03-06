<%
    ui.decorateWith("emr", "standardEmrPage", [ title: ui.message("emr.myaccount") ])

    ui.includeCss("mirebalais", "account.css")

    ui.includeJavascript("emr", "account/changePassword.js")

%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("emr.app.system.administration.myAccount.label")}", link: '${ui.pageLink("emr", "account/myAccount")}' },
        { label: "${ ui.message("emr.task.myAccount.changePassword.label")}" }

    ];

    var errorMessageOldPassword = "${ui.message("emr.account.changePassword.oldPassword.required")}";
    var errorMessageNewPassword = "${ui.message("emr.account.changePassword.newPassword.required")}";
    var errorMessageConfirmPassword = "${ui.message("emr.account.changePassword.confirmPassword.required")}";
    var errorMessageNewAndConfirmPassword = "${ui.message("emr.account.changePassword.newAndConfirmPassword.DoesNotMatch")}";
</script>

<h3>${ui.message("emr.myAccount.changePassword")}</h3>


<form method="post" id="accountForm">
    <fieldset>
        <legend>${ ui.message("emr.account.details") }</legend>

        <p id="oldPasswordSection" class="emr_passwordDetails">
            <label class="form-header" for="oldPassword">${ ui.message("emr.account.oldPassword") }</label>
            <input type="password" id="oldPassword" name="oldPassword"  autocomplete="off"/>
            ${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "oldPassword" ])}
        </p>

        <p id="newPasswordSection" class="emr_passwordDetails">
            <label class="form-header" for="newPassword">${ ui.message("emr.account.newPassword") }</label>
            <input type="password" id="newPassword" name="newPassword"  autocomplete="off"/>
            <label id="format-password">${ ui.message("emr.account.passwordFormat") }</label>
            ${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "newPassword" ])}
        </p>

        <p id="confirmPasswordSection" class="emr_passwordDetails">
            <label class="form-header" for="confirmPassword">${ ui.message("emr.user.confirmPassword") }</label>
            <input type="password" id="confirmPassword" name="confirmPassword"  autocomplete="off"/>
            ${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "confirmPassword" ])}
        </p>
    </fieldset>

    <div>
        <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="javascript:window.location='/${ contextPath }/emr/account/myAccount.page'" />
        <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
    </div>

</form>

