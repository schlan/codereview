package at.droelf.codereview

object Constants{

    val owner: String = "zendesk"

//    val repo: String = "zendesk_sdk_android"
//    val pullRequest: Int = 654

//    val repo: String = "zendesk_mobile_ios_sdk"
//    val pullRequest: Int = 670

    //zendesk_mobile_ios_sdk/pull/670


    val repo: String = "ZendeskAndroidClient"
    val pullRequest: Int = 537

    //https://github.com/zendesk/ZendeskAndroidClient/pull/537


    val rawFile: String =
"""
package com.zendesk.sdk.support;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.zendesk.logger.Logger;
import com.zendesk.sdk.R;
import com.zendesk.sdk.feedback.BaseZendeskFeedbackConfiguration;
import com.zendesk.sdk.feedback.WrappedZendeskFeedbackConfiguration;
import com.zendesk.sdk.feedback.ZendeskFeedbackConfiguration;
import com.zendesk.sdk.feedback.ui.ContactZendeskActivity;
import com.zendesk.sdk.model.helpcenter.Article;
import com.zendesk.sdk.model.request.Request;
import com.zendesk.sdk.model.settings.HelpCenterSettings;
import com.zendesk.sdk.model.settings.MobileSettings;
import com.zendesk.sdk.model.settings.SafeMobileSettings;
import com.zendesk.sdk.network.NetworkAware;
import com.zendesk.sdk.network.RequestProvider;
import com.zendesk.sdk.network.SdkSettingsProvider;
import com.zendesk.sdk.network.impl.ZendeskConfig;
import com.zendesk.sdk.network.impl.ZendeskRequestProvider;
import com.zendesk.sdk.network.impl.ZendeskSdkSettingsProvider;
import com.zendesk.sdk.requests.RequestListFragment;
import com.zendesk.sdk.storage.SdkStorage;
import com.zendesk.sdk.ui.NetworkAwareActionbarActivity;
import com.zendesk.sdk.util.NetworkUtils;
import com.zendesk.sdk.util.UiUtils;
import com.zendesk.service.ErrorResponse;
import com.zendesk.service.SafeZendeskCallback;
import com.zendesk.service.ZendeskCallback;
import com.zendesk.util.CollectionUtils;
import com.zendesk.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.zendesk.sdk.feedback.ui.ContactZendeskActivity.EXTRA_CONTACT_CONFIGURATION;


/**
 * The SupportActivity can be started without supplying additional extras or using {@link #startActivity(android.content.Context, String...)}
 * <p>
 *     The SupportActivity can be started without extras and it will start by listing all categories.
 *     If {@link #startActivity(android.content.Context, String...)} is used then the initial
 *     screen will be a list of Articles that match ALL of the label names specified.  If label names
 *     have been specified then these will also be applied to searching.
 * </p>
 * <p>
 *     The activity requires that a valid Zendesk Configuration is present before launching.
 * </p>
 *
 * <br>
 * Created by Zendesk on 09/09/2014.
 */
public class SupportActivity extends NetworkAwareActionbarActivity implements CategoriesListFragment.OnCategoryListFragmentListener,
SectionsListFragment.OnSectionListFragmentListener,
ArticlesListFragment.OnArticleListFragmentListener {


    private static final String LOG_TAG = SupportActivity.class.getName();

    private final static String FILTER_BY_STATUSES = "new,open,pending,hold,solved";

    private static final String EXTRA_STARTUP_CONFIG = "startup_config";
    private static final String EXTRA_SHOW_CONTACT_US = "extra_show_contact_button";

    private  static final String INITIAL_FRAGMENT_TAG = "initialfragment";

    private EditText mSearchEditText;

    private KeyboardSearchListener.Searchable mSearchable;
    private KeyboardSearchListener mSearchListener;

    private Menu mMenu;

    private SafeZendeskCallback<List<Request>> mHasRequestsCallback;
    private SafeZendeskCallback<MobileSettings> mRetryCallback;
    private View mProgressView;

    private String[] mLabels;
    private Long mCategoryId;
    private Long mSectionId;

    private StartConfiguration mStartConfiguration;
    private ZendeskFeedbackConfiguration mContactConfiguration;

    private boolean mShowContactUs;

    /**
     * Starts the SupportActivity using labels.
     * <p>
     *     In this mode the initial screen will be a list of all articles that match ALL of
     *     the supplied label names.  Searching will also be filtered by the same label names.
     * </p>
     *
     * @param context A valid context which will be used to start the Activity
     * @param labelNames Label names used to filter the initial list of articles and search by
     *
     * @deprecated Deprecated as of version 1.4.0.1 please use {@link Builder#listArticlesByLabels(String...)}
     */
    @Deprecated
    public static void startActivity(Context context, String... labelNames) {
        new Builder().listArticlesByLabels(labelNames).show(context);
    }

    /**
     * Starts the SupportActivity using a section Id.
     * <p>
     *     In this mode the initial screen will be a list of all articles that match the provided
     *     section id.
     * </p>
     *
     * @param context A valid context which will be used to start the Activity
     * @param sectionId A valid section id
     *
     * @deprecated Deprecated as of version 1.4.0.1 please use {@link Builder#listArticles(long)}
     */
    @Deprecated
    public static void startActivityWithSection(Context context, Long sectionId){
        new Builder().listArticles(sectionId).show(context);
    }

    /**
     * Starts the SupportActivity using a category Id.
     * <p>
     *     In this mode the initial screen will be a list of all sections that match the provided
     *     category id.
     * </p>
     *
     * @param context A valid context which will be used to start the Activity
     * @param categoryId A valid category id
     *
     * @deprecated Deprecated as of version 1.4.0.1 please use {@link Builder#listSections(long)}
     */
    @Deprecated
    public static void startActivityWithCategory(Context context, Long categoryId){
        new Builder().listSections(categoryId).show(context);
    }

    /**
     * Starts the SupportActivity using a category Id.
     * <p>
     *     In this mode the initial screen will be a list of all sections that match the provided
     *     category id.
     * </p>
     *
     * @param context A valid context which will be used to start the Activity
     * @param categoryId A valid category id
     *
     * @deprecated Deprecated as of version 1.3.0.1 please use {@link Builder#listSections(long)}
     */
    @Deprecated
    public static void startActivity(Context context, Long categoryId){
        new Builder().listSections(categoryId).show(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(LOG_TAG, "onCreate()");

        // See MSDK-509
        SdkStorage.INSTANCE.init(this);

        setContentView(R.layout.activity_support);

        if (StringUtils.isEmpty(ZendeskConfig.INSTANCE.getZendeskUrl())) {
            String error = "There is no Zendesk URL configured in ZendeskConfig. Did you call ZendeskConfig.INSTANCE.init()?";
            Logger.e(LOG_TAG, error);
            finish();
        }

        mShowContactUs = getIntent().getBooleanExtra(EXTRA_SHOW_CONTACT_US, true);

        if (getIntent().hasExtra(EXTRA_STARTUP_CONFIG)) {
            mStartConfiguration = (StartConfiguration) getIntent().getSerializableExtra(EXTRA_STARTUP_CONFIG);
        } else {
            mStartConfiguration = StartConfiguration.CATEGORIES;
        }


        boolean hasSuppliedContactConfiguration = getIntent().hasExtra(EXTRA_CONTACT_CONFIGURATION)
                && getIntent().getSerializableExtra(EXTRA_CONTACT_CONFIGURATION) instanceof ZendeskFeedbackConfiguration;
        if (hasSuppliedContactConfiguration) {
            mContactConfiguration = (ZendeskFeedbackConfiguration) getIntent().getSerializableExtra(EXTRA_CONTACT_CONFIGURATION);
        }

        switch (mStartConfiguration){
            case SECTIONS:
            mCategoryId = getIntent().getLongExtra(StartConfiguration.SECTIONS.extraDataKey, -1l);
            Logger.d(LOG_TAG, "Starting with a list of sections");
            break;
            case ARTICLES:
            mLabels = getIntent().getStringArrayExtra(StartConfiguration.ARTICLES.extraDataKey);
            Logger.d(LOG_TAG, "Starting with label names");
            break;
            case CATEGORIES:
            Logger.d(LOG_TAG, "Starting with categories");
            break;
            case ARTICLES_LIST:
            Logger.d(LOG_TAG, "Starting with a list of articles");
            mSectionId = getIntent().getLongExtra(StartConfiguration.ARTICLES_LIST.extraDataKey, -1l);
            break;
        }

        mSearchEditText = (EditText) findViewById(R.id.support_search_input);

        mProgressView = findViewById(R.id.support_activity_progress);


        /*
          This will avoid duplicated fragments if the system has discarded the activity due to
          low memory or if "Don't Keep Activities" is enabled in developer settings. savedInstanceState
          does have a FragmentState instance in the key "android:support:fragments" but the backing
          class is private and inaccessible.

          see MSDK-568
         */
        boolean hasSavedFragmentState = getSupportFragmentManager() != null
                && getSupportFragmentManager().findFragmentByTag(INITIAL_FRAGMENT_TAG) != null;

        if (!hasSavedFragmentState) {
            showFirstScreen();
        } else {
            Logger.d(LOG_TAG, "Skipping showFirstScreen() as we have saved fragment state");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
          If help center settings are unavailable show retry or no network toast message
         */
        HelpCenterSettings helpCenterSettings =
        ZendeskConfig.INSTANCE.getSettings().getSdkSettings().getHelpCenterSettings();

        if (helpCenterSettings == null) {
            if (mNetworkAvailable) {
                Logger.d(LOG_TAG, "Help Center settings are null. Network is available, allowing retry.");
                onRetryAvailable(getString(R.string.support_activity_unable_to_contact_support), new RetryClickListener());
            } else {
                Logger.d(LOG_TAG, "Help Center settings are null. Network is unavailable, retry not available.");
                onNetworkUnavailable();
            }
        }

        showCurrentFragment();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHasRequestsCallback != null) {
            mHasRequestsCallback.cancel();
            mHasRequestsCallback = null;
        }
        if (mRetryCallback != null) {
            mRetryCallback.cancel();
            mRetryCallback = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "Creating menu options");

        mMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_support_menu, menu);

        if (ScreenState.current != null) {
            switch (ScreenState.current) {
                case HELP_CENTER_SCREEN:
                Logger.d(LOG_TAG, "Current screen is help center, showing menu_contact");
                showOption(R.id.activity_support_menu_contact);
                break;
                case REQUESTS_SCREEN:
                Logger.d(LOG_TAG, "Current screen is requests, showing menu_new_contact");
                showOption(R.id.activity_support_menu_new_contact);
                break;
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Hides menu item
     *
     * @param id of menu item resource
     */
    private void hideOption(int id) {
        if (mMenu != null) {
            MenuItem item = mMenu.findItem(id);
            item.setVisible(false);
        }
    }

    /**
     * Shows menu item
     *
     * @param id of menu item resource
     */
    private void showOption(int id) {
        if (mMenu != null && mShowContactUs) {
            MenuItem item = mMenu.findItem(id);
            item.setVisible(true);
        }
    }

    private void disableSearch() {
        Logger.d(LOG_TAG, "disableSearch");
        UiUtils.setVisibility(mSearchEditText, View.GONE);
        mSearchable = null;
        mSearchListener = null;
        mSearchEditText.setText("");
    }

    private void enableSearch() {
        Logger.d(LOG_TAG, "enableSearch");
        UiUtils.setVisibility(mSearchEditText, View.VISIBLE);
        mSearchable = new SupportSearchListener();
        mSearchListener = new KeyboardSearchListener(mSearchEditText, mSearchable);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.activity_support_menu_contact) {
            switch (ScreenState.current) {
                case HELP_CENTER_SCREEN:
                showConversations();
                break;
                case REQUESTS_SCREEN:
                showContactActivity();
                break;
            }
            return true;
        } else if (itemId == R.id.activity_support_menu_new_contact) {
            showContactActivity();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Shows previous conversations if they are enabled in the settings and if there are any.
     * <p>
     * It will check for config settings if conversations are enabled. It will check if there are any previous conversations to show.
     * Otherwise it will show contact us activity.
     * </p>
     */
    private void showConversations() {

        SafeMobileSettings settings = new SafeMobileSettings(ZendeskConfig.INSTANCE.getSettings());

        if (settings.isConversationsEnabled()) {
            // hide search bar
            UiUtils.setVisibility(mSearchEditText, View.GONE);

            Logger.d(LOG_TAG, "Conversations enabled. Going to show existing user's tickets.");

            hideCurrentFragment();
            mProgressView.setVisibility(View.VISIBLE);

            RequestProvider provider = new ZendeskRequestProvider();
            mHasRequestsCallback = SafeZendeskCallback.from(new RequestsCallback());

            Logger.d(LOG_TAG, "Requesting the user's tickets");
            provider.getRequests(FILTER_BY_STATUSES, mHasRequestsCallback);
        } else {

            if (settings.isHelpCenterEnabled()) {
                Logger.d(LOG_TAG, "Conversations not enabled. Help Center is enabled. Resorting to Contact us activity.");
                showContactActivity();
            } else {
                Logger.d(LOG_TAG, "Conversations not enabled. Help Center not enabled. Resorting to Contact us activity and finishing current activity.");
                showContactActivityAndFinish();
            }
        }

    }

    private void hideCurrentFragment() {

        Logger.d(LOG_TAG, "Attempting to hide the current fragment");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.support_fragment_container);

        if (fragment != null && fragment.getView() != null) {
            Logger.d(LOG_TAG, "Setting the visibility to GONE");
            fragment.getView().setVisibility(View.GONE);
        }
    }

    private void showCurrentFragment() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.support_fragment_container);

        if (fragment != null && fragment.getView() != null) {
            View fragmentView = fragment.getView();

            if (fragmentView.getVisibility() != View.VISIBLE) {
                fragmentView.setVisibility(View.VISIBLE);

                if (fragment instanceof SupportListFragment) {
                    enableSearch();
                }

            }
        }
    }

    private void showContactActivity() {
        Intent intent = new Intent(this, ContactZendeskActivity.class);
        intent.putExtra(EXTRA_CONTACT_CONFIGURATION, mContactConfiguration);
        startActivity(intent);
    }

    /**
     * Use this when going back will result in a loop. I.e. when going back will always mean
     * that the current state has no other option but to launch contact again.
     */
    private void showContactActivityAndFinish() {
        Logger.d(LOG_TAG, "Showing contact activity and finishing the current one");
        showContactActivity();
        finish();
    }

    /**
     * Callback that checks if there are existing requests for this user
     */
    class RequestsCallback extends ZendeskCallback<List<Request>> {

        @Override
        public void onSuccess(List<Request> result) {
            mProgressView.setVisibility(View.GONE);

            if (CollectionUtils.isNotEmpty(result)) {
                ScreenState.current = ScreenState.current.next();

                // add requests list fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                RequestListFragment requestListFragment = new RequestListFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable("requests", new ArrayList<>(result));

                requestListFragment.setArguments(bundle);

                transaction.add(R.id.support_fragment_container, requestListFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                // swap menu item
                hideOption(R.id.activity_support_menu_contact);
                showOption(R.id.activity_support_menu_new_contact);

            } else {

                Logger.d(LOG_TAG, "There are no tickets to show.");

                HelpCenterSettings helpCenterSettings =
                ZendeskConfig.INSTANCE.getSettings().getSdkSettings().getHelpCenterSettings();

                if (helpCenterSettings.isEnabled()) {
                    showContactActivity();
                } else {
                    Logger.d(LOG_TAG, "Help Center is disabled in your SDK app's settings. Will show contact activity and finish current activity to prevent loops.");
                    showContactActivityAndFinish();
                }
            }
        }

        @Override
        public void onError(ErrorResponse error) {

            mProgressView.setVisibility(View.GONE);
            showCurrentFragment();

            Logger.e(LOG_TAG, "Could not get requests error. " + error.getReason());
            // fallback and show contact us
            showContactActivityAndFinish();
        }
    }

    @Override
    public void onCategorySelected(Long categoryId) {
        Logger.d(LOG_TAG, "onArticleSelected category : " + categoryId);
        transitionFragment(SectionsListFragment.newInstance(categoryId));
    }

    @Override
    public void onSectionSelected(Long sectionId) {
        Logger.d(LOG_TAG, "onSectionSelected section : " + sectionId);
        transitionFragment(ArticlesListFragment.newInstance(sectionId));
    }

    @Override
    public void onArticleSelected(Article article) {
        Logger.d(LOG_TAG, "onArticleSelected article : " + article);
        ViewArticleActivity.startActivity(this, article);
    }

    private void transitionFragment(Fragment fragment) {

        if (!NetworkUtils.isConnected(SupportActivity.this)) {
            Logger.d(LOG_TAG, "Ignoring fragment transition because there is no network connection");
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        transaction.replace(R.id.support_fragment_container, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    private void showFirstScreen() {
        HelpCenterSettings helpCenterSettings =
        ZendeskConfig.INSTANCE.getSettings().getSdkSettings().getHelpCenterSettings();

        /*
          If there is no help center settings - remove the search bar
         */
        if (helpCenterSettings == null) {
            Logger.e(LOG_TAG, "Can not show support content because there is no help center settings.");
            disableSearch();
            return;
        }

        if (!helpCenterSettings.isEnabled()) {
            Logger.d(LOG_TAG, "Help Center is disabled in your SDK app's settings. Showing conversations screen");
            disableSearch();

            // init state
            ScreenState.init(ScreenState.REQUESTS_SCREEN);

            showConversations();

        } else {

            Logger.d(LOG_TAG, "Help Center is enabled");
            enableSearch();

            // show support content
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            Fragment initialFragment = null;

            switch(mStartConfiguration){
                case CATEGORIES:
                initialFragment = CategoriesListFragment.newInstance();
                break;
                case SECTIONS:
                initialFragment = SectionsListFragment.newInstance(mCategoryId);
                break;
                case ARTICLES:
                initialFragment = LabeledArticlesListFragment.newInstance(mLabels);
                break;
                case ARTICLES_LIST:
                initialFragment = ArticlesListFragment.newInstance(mSectionId);
                break;
            }

            transaction.add(R.id.support_fragment_container, initialFragment, INITIAL_FRAGMENT_TAG);

            transaction.commit();

            // init state
            ScreenState.init(ScreenState.HELP_CENTER_SCREEN, ScreenState.REQUESTS_SCREEN);
        }
    }

    class SupportSearchListener implements KeyboardSearchListener.Searchable {

        @Override
        public void onSearchRequested(String query) {

            if (!NetworkUtils.isConnected(SupportActivity.this)) {
                Logger.d(LOG_TAG, "Ignoring search request as there is no network connection");
                return;
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.setTransition(FragmentTransaction.TRANSIT_NONE);

            Fragment currentFragment = fragmentManager.findFragmentById(R.id.support_fragment_container);

            /*
              We don't want multiple instances of search in the stack. Approved by Jonny.
             */
            if (currentFragment != null && (currentFragment instanceof ArticlesSearchResultsFragment)) {
                fragmentManager.popBackStack();
            }

            transaction.replace(R.id.support_fragment_container, ArticlesSearchResultsFragment.newInstance(query, mLabels));
            transaction.addToBackStack(null);
            transaction.commit();
        }

        @Override
        public void onSearchCleared() {
            FragmentManager fragmentManager = getSupportFragmentManager();

            Fragment currentFragment = fragmentManager.findFragmentById(R.id.support_fragment_container);

            if (currentFragment instanceof ArticlesSearchResultsFragment) {
                Logger.d(LOG_TAG, "Current fragment is search, triggering back press");
                onBackPressed();
            } else {
                Logger.d(LOG_TAG, "Current article is not search, ignoring onSearchCleared()");
            }
        }
    }

    @Override
    public void onNetworkAvailable() {
        super.onNetworkAvailable();

        /*
          This handles the case where the device was offline
         */
        HelpCenterSettings helpCenterSettings =
        ZendeskConfig.INSTANCE.getSettings().getSdkSettings().getHelpCenterSettings();

        if (helpCenterSettings == null) {
            onRetryAvailable(getString(R.string.support_activity_unable_to_contact_support), new RetryClickListener());
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.support_fragment_container);

        if (currentFragment instanceof NetworkAware) {
            Logger.d(LOG_TAG, "Dispatching onNetworkAvailable() to current fragment.");
            ((NetworkAware) currentFragment).onNetworkAvailable();
        }
    }

    @Override
    public void onNetworkUnavailable() {
        super.onNetworkUnavailable();

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.support_fragment_container);

        if (currentFragment instanceof NetworkAware) {
            Logger.d(LOG_TAG, "Dispatching onNetworkUnavailable() to current fragment.");
            ((NetworkAware) currentFragment).onNetworkUnavailable();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (ScreenState.current == null) {
            Logger.d(LOG_TAG, "back pressed, ScreenState.current is null, triggering finish()");
            finish();
            return;
        }

        switch (ScreenState.current) {
            case HELP_CENTER_SCREEN:
            /*
              Reset the listener before blanking text because otherwise we will have a double back
             */
            disableSearch();
            enableSearch();
            break;
            case REQUESTS_SCREEN:
            ScreenState.current = ScreenState.current.back();

            if (ScreenState.current == ScreenState.HELP_CENTER_SCREEN) {
                // prepare the help center elements
                showCurrentFragment();

                hideOption(R.id.activity_support_menu_new_contact);
                showOption(R.id.activity_support_menu_contact);
            } else {
                // no more screen states - just exit
                finish();
            }
            break;
            default:
            finish();
        }
    }

    /**
     * Screen state machine that keeps track of which screen state we move to.
     */
    enum ScreenState {

        /**
         * Defined states
         */
        HELP_CENTER_SCREEN, REQUESTS_SCREEN, ERROR;

        /**
         * Available states. Can be a subset of defined states.
         */
        private static LinkedList<ScreenState> available;

        private static final String TAG = ScreenState.class.getSimpleName();

        private static ScreenState current;

        /**
         * Initializes the state machine with all available states
         *
         * @param states The available states
         */
        static void init(ScreenState... states) {
            if (available != null) {
                Logger.w(LOG_TAG, "Reinitialising screen states");
            }

            available = new LinkedList<>(Arrays.asList(states));
            current = available.getFirst();
        }

        /**
         * Returns current state in the state machine
         *
         * @return the current state
         */
        ScreenState current() {
            return this;
        }

        /**
         * Defines next state
         *
         * @return state
         */
        ScreenState next() {
            Logger.d(LOG_TAG, "ScreenState::next()");

            try {
                int index = available.lastIndexOf(this);
                return available.get(++index);
            } catch (IndexOutOfBoundsException e) {
                Logger.w(TAG, "Illegal state transition, " + e.getMessage());
                return ERROR;
            } catch (NullPointerException npe) {
                Logger.w(TAG, "Have you initialized? " + npe.getMessage());
                return ERROR;
            }
        }

        /**
         * Defines previous state
         *
         * @return state
         */
        ScreenState back() {
            Logger.d(LOG_TAG, "ScreenState::back()");

            try {
                int index = available.lastIndexOf(this);
                return available.get(--index);
            } catch (IndexOutOfBoundsException e) {
                Logger.w(TAG, "Illegal state transition, " + e.getMessage());
                return ERROR;
            } catch (NullPointerException npe) {
                Logger.w(TAG, "Have you initialized? " + npe.getMessage());
                return ERROR;
            }
        }
    }

    class RetryClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Logger.e(LOG_TAG, "Retrying settings download.");

            onRetryUnavailable();
            mProgressView.setVisibility(View.VISIBLE);

            SdkSettingsProvider sdkSettingsProvider = new ZendeskSdkSettingsProvider();

            mRetryCallback = SafeZendeskCallback.from(new RetryCallback());
            sdkSettingsProvider.getSettings(mRetryCallback);
        }
    }

    /**
     * Callback that is used to retry getting settings from the server
     */
    class RetryCallback extends ZendeskCallback<MobileSettings> {

        @Override
        public void onSuccess(MobileSettings result) {

            mProgressView.setVisibility(View.GONE);

            Logger.d(LOG_TAG, "Settings retrieved");
            if (result.getSdkSettings().getHelpCenterSettings() == null) {
                Logger.d(LOG_TAG, "Settings has no help center config. Server issue?");
                onRetryAvailable(getString(R.string.support_activity_unable_to_contact_support), new RetryClickListener());
            } else {
                Logger.d(LOG_TAG, "Got help center settings. Going to show screen");
                showFirstScreen();
            }
        }

        @Override
        public void onError(ErrorResponse error) {
            mProgressView.setVisibility(View.GONE);
            onRetryAvailable(getString(R.string.support_activity_unable_to_contact_support), new RetryClickListener());
        }
    }

    /**
     * Represents the fragment which should be shown at startup
     */
    public enum StartConfiguration {

        /**
         *  Show all Categories
         */
        CATEGORIES(""),

        /**
         *  Show a list of sections according to a given category Id
         */
        SECTIONS("category_id"),

        /**
         *  Show a list of articles according to a given array of labels
         */
        ARTICLES("article_labels"),


        /**
         * Show a list of articles
         */
        ARTICLES_LIST("article_list");


        private final String extraDataKey;

        /**
         * Constructor taking the specified data key
         *
         * @param extraDataKey Data which is needed by a configuration, is stored with this key into the intent
         */
        StartConfiguration(String extraDataKey){
            this.extraDataKey = extraDataKey;
        }

        /**
         * Gets the key which is used to store the data in an intent
         *
         * @return the key which is used to store the data in an intent
         */
        public String getExtraDataKey() {
            return extraDataKey;
        }
    }


    /**
     * This is a builder class which is used to configure and start a {@link SupportActivity}
     *
     * <p>
     *     If calling {@link #show(Context)} without configuring anything, the following default
     *     behavior will be used:
     * <ol>
     *     <li>List categories</li>
     *     <li>Show contact us button</li>
     * </ol>
     * <p>
     *     Only one of the following four methods should be called: {@link #listArticlesByLabels(String...)},
     *     {@link #listCategories()}, {@link #listSections(long)}, {@link #listArticles(long)}
     *     <br>
     *     If calling multiple methods from the list above, the last call before invoking {@link #show(Context)}
     *     will define the final configuration of the shown SupportActivity.
     * </p>
     */
    public static class Builder {

        private final Bundle args;

        public Builder(){
            args = new Bundle();
        }

        /**
         * Starts SupportActivity, initially showing a list of articles that match to the given labels.
         *
         *  <p>
         *     In this mode the initial screen will be a list of all articles that match ALL of
         *     the supplied label names.  Searching will also be filtered by the same label names.
         * </p>
         *
         * @param labelNames Label names used to filter the initial list of articles and search by
         * @return The builder
         */
        public Builder listArticlesByLabels(String... labelNames){
            args.putSerializable(EXTRA_STARTUP_CONFIG, StartConfiguration.ARTICLES);
            args.putStringArray(StartConfiguration.ARTICLES.extraDataKey, labelNames);
            return this;
        }

        /**
         * Starts SupportActivity, showing a list of categories.
         *
         * @return The builder
         */
        public Builder listCategories(){
            args.putSerializable(EXTRA_STARTUP_CONFIG, StartConfiguration.CATEGORIES);
            return this;
        }

        /**
         * Starts SupportActivity, initially showing a list of sections that match the provided category Id.
         *
         * @param categoryId A valid category Id
         * @return The builder
         */
        public Builder listSections(long categoryId){
            args.putSerializable(EXTRA_STARTUP_CONFIG, StartConfiguration.SECTIONS);
            args.putLong(StartConfiguration.SECTIONS.getExtraDataKey(), categoryId);
            return this;
        }

        /**
         * Starts SupportActivity initially showing a list of articles that match the provided section Id.
         *
         * @param sectionId A valid section Id
         * @return The builder
         */
        public Builder listArticles(long sectionId){
            args.putSerializable(EXTRA_STARTUP_CONFIG, StartConfiguration.ARTICLES_LIST);
            args.putLong(StartConfiguration.ARTICLES_LIST.getExtraDataKey(), sectionId);
            return this;
        }

        /**
         * Define if the conversations/create ticket button located in the Actionbar should
         * be visible or not.
         *
         * @param show Pass in true if the button should be shown, false if it should be hidden
         * @return The builder
         */
        public Builder showContactUsButton(boolean show){
            args.putBoolean(EXTRA_SHOW_CONTACT_US, show);
            return this;
        }

        /**
         * Sets the Configuration for the Contact component, if a non-null instance of {@link ZendeskFeedbackConfiguration}
         * this will be used to provide a set of values to the Contact component to enhance the request creation.
         * @param configuration instance of {@link ZendeskFeedbackConfiguration} to be passed to the configuration
         * @return The builder
         */
        public Builder withContactConfiguration(ZendeskFeedbackConfiguration configuration) {

            if (configuration != null) {
                configuration = new WrappedZendeskFeedbackConfiguration(configuration);
            }

            args.putSerializable(EXTRA_CONTACT_CONFIGURATION, configuration);
            return this;
        }

        /**
         * Show {@link SupportActivity} with the specified options.
         *
         * @param context A valid context
         */
        public void show(Context context){
            context.startActivity(intent(context));
        }

        /**
         * Creates an Intent for starting SupportActivity, containing all the options configured
         * before.
         *
         * @param context A valid context
         * @return An Intent for stating SupportActivity
         */
        Intent intent(Context context){
            final Intent intent = new Intent(context, SupportActivity.class);
            intent.putExtras(args);
            return intent;
        }
    }
}
"""

    val patch: String =
"""
@@ -15,6 +15,9 @@

 import com.zendesk.logger.Logger;
 import com.zendesk.sdk.R;
+import com.zendesk.sdk.feedback.BaseZendeskFeedbackConfiguration;
+import com.zendesk.sdk.feedback.WrappedZendeskFeedbackConfiguration;
+import com.zendesk.sdk.feedback.ZendeskFeedbackConfiguration;
 import com.zendesk.sdk.feedback.ui.ContactZendeskActivity;
 import com.zendesk.sdk.model.helpcenter.Article;
 import com.zendesk.sdk.model.request.Request;
@@ -43,6 +46,8 @@
 import java.util.LinkedList;
 import java.util.List;

+import static com.zendesk.sdk.feedback.ui.ContactZendeskActivity.EXTRA_CONTACT_CONFIGURATION;
+

 /**
  * The SupportActivity can be started without supplying additional extras or using {@link #startActivity(android.content.Context, String...)}
@@ -89,6 +94,7 @@
     private Long mSectionId;

     private StartConfiguration mStartConfiguration;
+    private ZendeskFeedbackConfiguration mContactConfiguration;

     private boolean mShowContactUs;

@@ -179,12 +185,19 @@ protected void onCreate(Bundle savedInstanceState) {

         mShowContactUs = getIntent().getBooleanExtra(EXTRA_SHOW_CONTACT_US, true);

-        if(getIntent().hasExtra(EXTRA_STARTUP_CONFIG)){
+        if (getIntent().hasExtra(EXTRA_STARTUP_CONFIG)) {
             mStartConfiguration = (StartConfiguration) getIntent().getSerializableExtra(EXTRA_STARTUP_CONFIG);
-        }else{
+        } else {
             mStartConfiguration = StartConfiguration.CATEGORIES;
         }

+
+        boolean hasSuppliedContactConfiguration = getIntent().hasExtra(EXTRA_CONTACT_CONFIGURATION)
+                && getIntent().getSerializableExtra(EXTRA_CONTACT_CONFIGURATION) instanceof ZendeskFeedbackConfiguration;
+        if (hasSuppliedContactConfiguration) {
+            mContactConfiguration = (ZendeskFeedbackConfiguration) getIntent().getSerializableExtra(EXTRA_CONTACT_CONFIGURATION);
+        }
+
         switch (mStartConfiguration){
             case SECTIONS:
                 mCategoryId = getIntent().getLongExtra(StartConfiguration.SECTIONS.extraDataKey, -1l);
@@ -415,6 +428,7 @@ private void showCurrentFragment() {

     private void showContactActivity() {
         Intent intent = new Intent(this, ContactZendeskActivity.class);
+        intent.putExtra(EXTRA_CONTACT_CONFIGURATION, mContactConfiguration);
         startActivity(intent);
     }

@@ -852,6 +866,7 @@ public void onError(ErrorResponse error) {
         private final String extraDataKey;

         /**
+         * Constructor taking the specified data key
          *
          * @param extraDataKey Data which is needed by a configuration, is stored with this key into the intent
          */
@@ -859,6 +874,11 @@ public void onError(ErrorResponse error) {
             this.extraDataKey = extraDataKey;
         }

+        /**
+         * Gets the key which is used to store the data in an intent
+         *
+         * @return the key which is used to store the data in an intent
+         */
         public String getExtraDataKey() {
             return extraDataKey;
         }
@@ -955,6 +975,22 @@ public Builder showContactUsButton(boolean show){
         }

         /**
+         * Sets the Configuration for the Contact component, if a non-null instance of {@link ZendeskFeedbackConfiguration}
+         * this will be used to provide a set of values to the Contact component to enhance the request creation.
+         * @param configuration instance of {@link ZendeskFeedbackConfiguration} to be passed to the configuration
+         * @return The builder
+         */
+        public Builder withContactConfiguration(ZendeskFeedbackConfiguration configuration) {
+
+            if (configuration != null) {
+                configuration = new WrappedZendeskFeedbackConfiguration(configuration);
+            }
+
+            args.putSerializable(EXTRA_CONTACT_CONFIGURATION, configuration);
+            return this;
+        }
+
+        /**
          * Show {@link SupportActivity} with the specified options.
          *
          * @param context A valid context
@@ -975,7 +1011,5 @@ Intent intent(Context context){
             intent.putExtras(args);
             return intent;
         }
-
     }
-
-}
+}
"""

}