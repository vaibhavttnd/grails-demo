package com.tweetAmp

class UserController {

    def index(String q) {
        List<User> users = User.createCriteria().list(max: params.max, offset: params.offset) {
            if (q) {
                or {
                    ilike('email', "%${q}%")
                    ilike('name', "%${q}%")
                }
            }
        }
        [users: users, userInstanceCount: users.totalCount]
    }
}