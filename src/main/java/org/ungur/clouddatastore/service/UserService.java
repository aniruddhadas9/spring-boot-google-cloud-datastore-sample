package org.ungur.clouddatastore.service;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.datastore.*;
import org.springframework.scheduling.annotation.Async;
import org.ungur.clouddatastore.model.BatchUser;
import org.ungur.clouddatastore.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    Datastore datastore;

    private KeyFactory userKeyFactory;

    @PostConstruct
    public void initializeKeyFactories() {
        log.info("Initializing key factories");
        userKeyFactory = datastore.newKeyFactory().setKind("User");
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<User>();
        Query<Entity> query = Query.newEntityQueryBuilder()
            .setKind("User")
            .setOrderBy(StructuredQuery.OrderBy.desc("email"))
            .build();
        QueryResults<Entity> tasks = datastore.run(query);
        while (tasks.hasNext()){
            Entity entity = tasks.next();
            System.out.println(entity);
            User user = new User();

            user.setId(entity.getKey().getName());
            user.setEmail(entity.getString("email"));
            user.setFullName(entity.getString("fullName"));
            user.setPassword(entity.getString("password"));
            users.add(user);
        }
        return users;
    }

    public Entity createUser(User user) {
        return datastore.put(createUserEntity(user));
    }

    public Batch.Response createUser(BatchUser users) {
        Batch batch = datastore.newBatch();
        for (User user : users.getUsers()) {
            batch.put(createUserEntity(user));
        }

        return batch.submit();
    }

    private Entity createUserEntity(User user) {
        Key key = userKeyFactory.newKey(user.getId());
        return Entity.newBuilder(key)
                .set("email", user.getEmail())
                .set("password", user.getPassword())
                .set("fullName", user.getFullName())
                .set("age", user.getAge())
                .build();
    }

    @Async
    public void updateUser(String id, User user) {
        //
    }

    @Async
    public void deleteUser(String id) {
        //
    }

}
