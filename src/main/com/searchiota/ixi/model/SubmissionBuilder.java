package com.searchiota.ixi.model;

import com.searchiota.ixi.utils.KeyPair;
import org.json.JSONException;

public class SubmissionBuilder {

    public String username;
	public IotaNode mynode;
	public String channel;
    public KeyPair keyPair;

    public Submission build() throws JSONException {
        return new Submission(this);
    }
}