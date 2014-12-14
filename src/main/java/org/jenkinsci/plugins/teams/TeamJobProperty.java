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

import com.google.common.base.Optional;
import hudson.Extension;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tommy Bo <tommy.bo@visma.com>
 */
public class TeamJobProperty extends JobProperty<AbstractProject<?,?>> {

	private final String responsibleTeam;

	@DataBoundConstructor
	public TeamJobProperty(String responsibleTeam) {
		this.responsibleTeam = responsibleTeam;
	}

	public String getResponsibleTeam() {
		return responsibleTeam;
	}

	@Override
	public TeamPluginDescriptor getDescriptor() {
		return (TeamPluginDescriptor) super.getDescriptor();
	}

	@Extension
	public static final class TeamPluginDescriptor extends JobPropertyDescriptor {

		private List<Team> teams;

		public TeamPluginDescriptor() {
			super(TeamJobProperty.class);
			load();
		}
		public FormValidation doCheckTeamName(@QueryParameter String value)
						throws IOException, ServletException {
			if (value.length() == 0) {
				return FormValidation.error("Please provide a teamname");
			}
			return FormValidation.ok();
		}

		@Override
		public boolean isApplicable(Class<? extends Job> jobType) {
			return true;
		}

		public String getDisplayName() {
			return "Teamster";
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
			req.bindJSON(this, formData);
			save();
			return true;
		}

		public Optional<Team> getTeamByName(String teamName) {
			for (Team team : getTeams()) {
				if(team.getTeamName().equals(teamName))
					return Optional.of(team);
			}
			return Optional.absent();
		}

		public List<Team> getTeams() {
			if(teams == null) {
				teams = new ArrayList<Team>();
			}
			return teams;
		}

		public void setTeams(List<Team> teams) {
			this.teams = teams;
		}

		public ListBoxModel doFillResponsibleTeamItems() {
			ListBoxModel list = new ListBoxModel();
			for (Team team : getTeams()) {
				list.add(team.getTeamName());
			}
			return list;
		}
	}
}
