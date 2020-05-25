package com.adeptj.modules.data.mybatis.core;

import com.adeptj.modules.data.mybatis.MyBatisRepository;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

import java.util.List;
import java.util.function.Function;

@ConsumerType
public abstract class AbstractMyBatisRepository<T, ID> implements MyBatisRepository<T, ID> {

    protected SqlSessionFactory sessionFactory;

    public void setSessionFactory(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public T findById(String statement, ID id) {
        try (SqlSession session = this.sessionFactory.openSession()) {
            return session.selectOne(statement, id);
        }
    }

    @Override
    public List<T> findAll(String statement) {
        try (SqlSession session = this.sessionFactory.openSession()) {
            return session.selectList(statement);
        }
    }

    @Override
    public void insert(String statement, T object) {
        try (SqlSession session = this.sessionFactory.openSession()) {
            session.insert(statement, object);
            session.commit();
        }
    }

    @Override
    public void deleteById(String statement, ID id) {
        try (SqlSession session = this.sessionFactory.openSession()) {
            session.delete(statement, id);
            session.commit();
        }
    }

    @Override
    public T doInSession(@NotNull Function<SqlSession, T> function) {
        try (SqlSession session = this.sessionFactory.openSession()) {
            return function.apply(session);
        }
    }

    @Override
    public T doInSessionCommit(@NotNull Function<SqlSession, T> function) {
        try (SqlSession session = this.sessionFactory.openSession()) {
            T result = function.apply(session);
            session.commit();
            return result;
        }
    }
}