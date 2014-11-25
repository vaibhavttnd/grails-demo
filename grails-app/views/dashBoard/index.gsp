<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>TweetAmp</title>
    <script type="text/javascript">
        var retweetUrl = "${createLink(controller: 'dashBoard' ,action: 'reTweetForm')}";
    </script>
    <asset:javascript src="/plugins/chosen/chosen.jquery.js"/>
    <asset:stylesheet src="/plugins/chosen/chosen.css"/>
</head>

<body>
<sec:ifAllGranted roles="ROLE_ADMIN">
    <g:render template="adminDashboard"/>
</sec:ifAllGranted>
<div class="sub-container">
    <div class="float-right font-white">
        <h2>Accounts</h2>
        <g:if test="${accessToken?.getScreenName()}">
            <p>Thanks for registering with us.</p>
            <asset:image src="twitterIcon.jpeg"
                         style="width: 30px; height: 25px;"/> @${accessToken?.getScreenName()}
            <ul class="nav">
                <g:link controller="dashBoard" action="revokeApp" class="btn btn-danger"
                        onclick="return confirm('Are you sure you want to revoke your twitter access?')">Revoke Access</g:link>
            </ul>
        </g:if>
        <g:else>
            <p>Some text.</p>
            <g:link controller="dashBoard" action="signInTwitter">
                <img title="Sign in with Twitter" alt="Sign in with Twitter"
                     src="https://g.twimg.com/dev/sites/default/files/images_documentation/sign-in-with-twitter-gray.png">
            </g:link>
        </g:else>
    </div>
</div>
</body>

</html>