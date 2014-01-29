package net.trolldad.dashclock.redditheadlines.otto;

import com.squareup.otto.Bus;

import org.androidannotations.annotations.EBean;

/**
 * Created by jacob-tabak on 1/25/14.
 */
@EBean(scope = EBean.Scope.Singleton)
public class MyBus extends Bus { }
