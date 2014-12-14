/*
 * The MIT License
 *
 * Copyright 2014 Tommy Bo <tommy.bo@visma.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.teams;

import hudson.Extension;
import hudson.model.User;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.teams.TeamJobProperty.TeamPluginDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 *
 * @author Tommy Bo <tommy.bo@visma.com>
 */
@ExportedBean(defaultVisibility = 999)
public class TeamUserProperty extends UserProperty {
	private String teamName;

	public TeamUserProperty() {
	}

	@DataBoundConstructor
	public TeamUserProperty(String teamName) {
		this.teamName = teamName;
	}

	@Exported
	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	@Override
	public TeamUserDescription getDescriptor() {
		return (TeamUserDescription) super.getDescriptor();
	}

	@Extension
	public static final class TeamUserDescription extends UserPropertyDescriptor {

    public TeamUserDescription() {
        super(TeamUserProperty.class);
    }

		@Override
		public UserProperty newInstance(StaplerRequest req, JSONObject formData) throws FormException {
			return req.bindJSON(TeamUserProperty.class, formData);
		}

		@Override
		public UserProperty newInstance(User user) {
			return new TeamUserProperty();
		}

		@Override
		public String getDisplayName() {
			return "TeamUserProperty";
		}

		public ListBoxModel doFillTeamNameItems() {
			return ((TeamPluginDescriptor) Jenkins.getInstance().getDescriptorOrDie(TeamJobProperty.class)).doFillResponsibleTeamItems();
		}
	}
}
