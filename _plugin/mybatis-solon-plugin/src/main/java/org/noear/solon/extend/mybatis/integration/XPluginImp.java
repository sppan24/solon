package org.noear.solon.extend.mybatis.integration;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.apache.ibatis.ext.solon.Db;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {

        app.onEvent(BeanWrap.class, new DsEventListener());

        Aop.context().beanBuilderAdd(Db.class, (clz, wrap, anno) -> {
            if (clz.isInterface() == false) {
                return;
            }

            if (Utils.isEmpty(anno.value())) {
                Aop.getAsyn(DataSource.class, (dsBw) -> {
                    create0(clz, dsBw);
                });
            } else {
                Aop.getAsyn(anno.value(), (dsBw) -> {
                    if (dsBw.raw() instanceof DataSource) {
                        create0(clz, dsBw);
                    }
                });
            }
        });

        Aop.context().beanInjectorAdd(Db.class, (varH, anno) -> {
            if (Utils.isEmpty(anno.value())) {
                Aop.getAsyn(DataSource.class, (dsBw) -> {
                    inject0(varH, dsBw);
                });
            } else {
                Aop.getAsyn(anno.value(), (dsBw) -> {
                    if (dsBw.raw() instanceof DataSource) {
                        inject0(varH, dsBw);
                    }
                });
            }
        });
    }



    private void create0(Class<?> clz, BeanWrap dsBw) {
        SqlSessionProxy proxy = SqlSessionManager.global().get(dsBw);

        Object raw = proxy.getMapper(clz);
        Aop.wrapAndPut(clz,raw);
    }

    private void inject0(VarHolder varH, BeanWrap dsBw) {
        SqlSessionProxy proxy = SqlSessionManager.global().get(dsBw);

        if (SqlSession.class.isAssignableFrom(varH.getType())) {
            varH.setValue(proxy);
            return;
        }

        if (SqlSessionFactory.class.isAssignableFrom(varH.getType())) {
            varH.setValue(proxy.getFactory());
            return;
        }

        if (varH.getType().isInterface()) {
            Object mapper = proxy.getMapper(varH.getType());

            varH.setValue(mapper);
            return;
        }
    }
}
