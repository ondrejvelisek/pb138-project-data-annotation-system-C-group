package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public interface Dao<B extends Object> {

    void create(B bean) throws DaoException;

    B getById(Long id) throws DaoException;

    void update(B bean) throws DaoException;

    void delete(B bean) throws DaoException;

}
