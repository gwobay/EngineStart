package com.prod.intelligent7.engineautostart;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SendCommandService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_REGISTRATION = "com.prod.intelligent7.engineautostart.action.REGISTRATION";
    private static final String ACTION_DO_IT = "com.prod.intelligent7.engineautostart.action.DO_IT";

    // TODO: Rename parameters
    private static final String EXTRA_COMMAND = "com.prod.intelligent7.engineautostart.extra.COMMAND";
    private static final String EXTRA_PARAMETERS = "com.prod.intelligent7.engineautostart.extra.PARAMETERS";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionRegistration(Context context, String param1, String param2) {
        Intent intent = new Intent(context, SendCommandService.class);
        intent.setAction(ACTION_REGISTRATION);
        intent.putExtra(EXTRA_COMMAND, param1);
        intent.putExtra(EXTRA_PARAMETERS, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionDoIt(Context context, String param1, String param2) {
        Intent intent = new Intent(context, SendCommandService.class);
        intent.setAction(ACTION_DO_IT);
        intent.putExtra(EXTRA_COMMAND, param1);
        intent.putExtra(EXTRA_PARAMETERS, param2);
        context.startService(intent);
    }

    public SendCommandService() {
        super("SendCommandService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REGISTRATION.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_COMMAND);
                final String param2 = intent.getStringExtra(EXTRA_PARAMETERS);
                handleActionFoo(param1, param2);
            } else if (ACTION_DO_IT.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_COMMAND);
                final String param2 = intent.getStringExtra(EXTRA_PARAMETERS);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
