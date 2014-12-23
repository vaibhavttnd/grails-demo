<%@ page import="com.tweetAmp.User" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="admin">
    <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<div class="panel panel-primary" style="margin-left: 15px;">
    <div class="panel-heading">
        Show User
        <ul class="navbar-top-links pull-right">
            <li>
                <g:link action="list" class="text-white"><i class="icon-list"></i>&nbsp;<g:message code="default.list.label"
                                                                                args="[entityName]"/></g:link>
            </li>
        </ul>
    </div>
    <table class="table table-striped table-hover table-bordered margin-t10">

        <tr>
            <g:if test="${userInstance?.username}">
                <td><strong><g:message code="user.username.label" default="Username"/></strong></td>

                <td><g:fieldValue bean="${userInstance}" field="username"/></td>

            </g:if>
        </tr>

        <tr>
            <g:if test="${userInstance?.email}">
                <td><strong><g:message code="user.email.label" default="Email"/></strong></td>

                <td><g:fieldValue bean="${userInstance}" field="email"/></td>

            </g:if>
        </tr>

        <tr>
            <g:if test="${userInstance?.twitterUser}">
                <td><strong><g:message code="user.twitterCredential.label" default="Twitter Credential"/></strong></td>

                <td><g:link controller="twitterCredential" action="show"
                            id="${userInstance?.twitterUser?.id}">${userInstance?.twitterUser?.
                            encodeAsHTML()}</g:link></td>

            </g:if>
        </tr>

        <tr>
            <g:if test="${userInstance?.categories}">
                <td><strong><g:message code="user.categories.label" default="Categories"/></strong></td>

                <td><ul class="unstyled"><g:each in="${userInstance.categories}" var="c">
                    <li><g:link controller="category" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></li>
                </g:each></ul></td>

            </g:if>
        </tr>

        <tr>
            <td><strong><g:message code="user.role.label" default="Role"/></strong></td>
            <td>
                <ul class="unstyled">
                    <g:each in="${userInstance?.authorities}" var="role">
                        <li>
                            ${role.authority}
                        </li>
                    </g:each>
                </ul>
            </td>
        </tr>

        <tr>
            <g:if test="${userInstance?.accountExpired || true}">
                <td><strong><g:message code="user.accountExpired.label" default="Account Expired"/></strong></td>

                <td><g:formatBoolean boolean="${userInstance?.accountExpired}"/></td>

            </g:if>
        </tr>

        <tr>
            <g:if test="${userInstance?.accountLocked || true}">
                <td><strong><g:message code="user.accountLocked.label" default="Account Locked"/></strong></td>

                <td><g:formatBoolean boolean="${userInstance?.accountLocked}"/></td>

            </g:if>
        </tr>

        <tr>
            <g:if test="${userInstance?.enabled || true}">
                <td><strong><g:message code="user.enabled.label" default="Enabled"/></strong></td>

                <td><g:formatBoolean boolean="${userInstance?.enabled}"/></td>

            </g:if>
        </tr>

        <tr>
            <g:if test="${userInstance?.passwordExpired || true}">
                <td><strong><g:message code="user.passwordExpired.label" default="Password Expired"/></strong></td>

                <td><g:formatBoolean boolean="${userInstance?.passwordExpired}"/></td>

            </g:if>
        </tr>

    </table>
    <g:form class="margin-20">
        <g:hiddenField name="id" value="${userInstance?.id}"/>
        <g:link class="btn btn-info" action="edit"
                id="${userInstance?.id}"><g:message code="default.button.edit.label" default="Edit"/>&nbsp;<i
                class="icon-edit icon-white"></i></g:link>
        <g:actionSubmit class="btn btn-danger" action="delete"
                        value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                        onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
    </g:form>

</div>
</body>
</html>
