<%@ page import="com.tweetAmp.Category" %>



            <div class="control-group">
                <label class="control-label hidden-phone" for="name">
                    <g:message code="category.name.label" default="Name" />
                    
                </label>
                <div class="controls">
                    <g:textField name="name" required="" value="${categoryInstance?.name}"/>

                </div>
            </div>

            <div class="control-group">
                <label class="control-label hidden-phone" for="description">
                    <g:message code="category.description.label" default="Desc" />
                    
                </label>
                <div class="controls">
                    <g:textField name="description" value="${categoryInstance?.description}"/>

                </div>
            </div>

            <div class="control-group">
                <label class="control-label hidden-phone" for="users">
                    <g:message code="category.users.label" default="Users" />
                    
                </label>
                <div class="controls">
                    <g:select name="users" from="${com.tweetAmp.User.list()}" multiple="multiple" optionKey="id" size="5" value="${categoryInstance?.users*.id}" class="many-to-many"/>

                </div>
            </div>

