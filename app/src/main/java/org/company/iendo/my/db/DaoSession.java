package org.company.iendo.my.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import org.company.iendo.bean.beandb.ServersDBBean;
import org.company.iendo.bean.beandb.UserDBBean;
import org.company.iendo.bean.beandb.UserDetailMSGDBBean;
import org.company.iendo.bean.beandb.image.ImageListDownDBBean;

import org.company.iendo.my.db.ServersDBBeanDao;
import org.company.iendo.my.db.UserDBBeanDao;
import org.company.iendo.my.db.UserDetailMSGDBBeanDao;
import org.company.iendo.my.db.ImageListDownDBBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig serversDBBeanDaoConfig;
    private final DaoConfig userDBBeanDaoConfig;
    private final DaoConfig userDetailMSGDBBeanDaoConfig;
    private final DaoConfig imageListDownDBBeanDaoConfig;

    private final ServersDBBeanDao serversDBBeanDao;
    private final UserDBBeanDao userDBBeanDao;
    private final UserDetailMSGDBBeanDao userDetailMSGDBBeanDao;
    private final ImageListDownDBBeanDao imageListDownDBBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        serversDBBeanDaoConfig = daoConfigMap.get(ServersDBBeanDao.class).clone();
        serversDBBeanDaoConfig.initIdentityScope(type);

        userDBBeanDaoConfig = daoConfigMap.get(UserDBBeanDao.class).clone();
        userDBBeanDaoConfig.initIdentityScope(type);

        userDetailMSGDBBeanDaoConfig = daoConfigMap.get(UserDetailMSGDBBeanDao.class).clone();
        userDetailMSGDBBeanDaoConfig.initIdentityScope(type);

        imageListDownDBBeanDaoConfig = daoConfigMap.get(ImageListDownDBBeanDao.class).clone();
        imageListDownDBBeanDaoConfig.initIdentityScope(type);

        serversDBBeanDao = new ServersDBBeanDao(serversDBBeanDaoConfig, this);
        userDBBeanDao = new UserDBBeanDao(userDBBeanDaoConfig, this);
        userDetailMSGDBBeanDao = new UserDetailMSGDBBeanDao(userDetailMSGDBBeanDaoConfig, this);
        imageListDownDBBeanDao = new ImageListDownDBBeanDao(imageListDownDBBeanDaoConfig, this);

        registerDao(ServersDBBean.class, serversDBBeanDao);
        registerDao(UserDBBean.class, userDBBeanDao);
        registerDao(UserDetailMSGDBBean.class, userDetailMSGDBBeanDao);
        registerDao(ImageListDownDBBean.class, imageListDownDBBeanDao);
    }
    
    public void clear() {
        serversDBBeanDaoConfig.clearIdentityScope();
        userDBBeanDaoConfig.clearIdentityScope();
        userDetailMSGDBBeanDaoConfig.clearIdentityScope();
        imageListDownDBBeanDaoConfig.clearIdentityScope();
    }

    public ServersDBBeanDao getServersDBBeanDao() {
        return serversDBBeanDao;
    }

    public UserDBBeanDao getUserDBBeanDao() {
        return userDBBeanDao;
    }

    public UserDetailMSGDBBeanDao getUserDetailMSGDBBeanDao() {
        return userDetailMSGDBBeanDao;
    }

    public ImageListDownDBBeanDao getImageListDownDBBeanDao() {
        return imageListDownDBBeanDao;
    }

}
