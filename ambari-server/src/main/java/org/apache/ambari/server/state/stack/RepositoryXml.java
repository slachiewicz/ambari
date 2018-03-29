/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.state.stack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.ambari.server.stack.Validable;
import org.apache.ambari.server.state.RepositoryInfo;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Represents the repository file <code>$STACK_VERSION/repos/repoinfo.xml</code>.
 */
@XmlRootElement(name="reposinfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class RepositoryXml implements Validable{

  @XmlElement(name="latest")
  private String latestUri;

  @XmlElement(name="os")
  private List<Os> oses = new ArrayList<>();

  @XmlTransient
  private boolean valid = true;

  /**
   *
   * @return valid xml flag
   */
  @Override
  public boolean isValid() {
    return valid;
  }

  /**
   *
   * @param valid set validity flag
   */
  @Override
  public void setValid(boolean valid) {
    this.valid = valid;
  }

  @XmlTransient
  private Set<String> errorSet = new HashSet<>();

  @Override
  public void addError(String error) {
    errorSet.add(error);
  }

  @Override
  public Collection<String> getErrors() {
    return errorSet;
  }

  @Override
  public void addErrors(Collection<String> errors) {
    errorSet.addAll(errors);
  }

  /**
   * @return the latest URI defined, if any.
   */
  public String getLatestURI() {
    return latestUri;
  }

  /**
   * @return the list of <code>os</code> elements.
   */
  public List<Os> getOses() {
    return oses;
  }

  /**
   * The <code>os</code> tag.
   */
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class Os {
    @XmlAttribute(name="family")
    @JsonProperty("os_type")
    private String family;

    @XmlElement(name="package-version")
    private String packageVersion;

    @XmlElement(name="repo")
    @JsonProperty("repositories")
    private List<Repo> repos;

    private Os() {
    }

    /**
     * @return the os family
     */
    public String getFamily() {
      return family;
    }

    /**
     * @return the list of repo elements
     */
    public List<Repo> getRepos() {
      return repos;
    }

    /**
     * @return the package version, or {@code null} if not defined
     */
    public String getPackageVersion() {
      return packageVersion;
    }
  }

  /**
   * The <code>repo</code> tag.
   */
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class Repo {
    @JsonProperty("base_url")
    private String baseurl = null;

    @JsonProperty("mirrors_list")
    private String mirrorslist = null;

    @JsonProperty("repo_id")
    private String repoid = null;

    @JsonProperty("repo_name")
    private String reponame = null;

    @JsonProperty("distribution")
    private String distribution = null;

    @JsonProperty("components")
    private String components = null;

    @JsonProperty("unique")
    private boolean unique = false;

    @XmlElementWrapper(name="tags")
    @XmlElement(name="tag")
    @JsonProperty("tags")
    private Set<RepoTag> tags = new HashSet<>();

    private Repo() {
    }

    /**
     * @return the base url
     */
    @JsonProperty("base_url")
    public String getBaseUrl() {
      return (null == baseurl || baseurl.isEmpty()) ? null : baseurl;
    }

    /**
     * @return the mirrorlist field
     */
    @JsonProperty("mirrors_list")
    public String getMirrorsList() {
      return (null == mirrorslist || mirrorslist.isEmpty()) ? null : mirrorslist;
    }

    /**
     * @return the repo id
     */
    @JsonProperty("repo_id")
    public String getRepoId() {
      return repoid;
    }

    /**
     * @return the repo name
     */
    @JsonProperty("repo_name")
    public String getRepoName() {
      return reponame;
    }

    @JsonProperty("distribution")
    public String getDistribution() {
      return distribution;
    }

    @JsonProperty("components")
    public String getComponents() {
      return components;
    }

    /**
     * @return true if version of HDP that change with each release
     */
    @JsonProperty("unique")
    public boolean isUnique() {
      return unique;
    }

    /**
     * @param unique set is version of HDP that change with each release
     */
    public void setUnique(boolean unique) {
      this.unique = unique;
    }

    /**
     * @return the repo tags
     */
    @JsonProperty("tags")
    public Set<RepoTag> getTags() {
      return tags;
    }
  }

  /**
   * @return the list of repositories consumable by the web service.
   */
  public List<RepositoryInfo> getRepositories() {
    List<RepositoryInfo> repos = new ArrayList<>();

    for (RepositoryXml.Os o : getOses()) {
      String osFamily = o.getFamily();
      for (String os : osFamily.split(",")) {
        for (RepositoryXml.Repo r : o.getRepos()) {

          RepositoryInfo ri = new RepositoryInfo();
          ri.setBaseUrl(r.getBaseUrl());
          ri.setDefaultBaseUrl(r.getBaseUrl());
          ri.setMirrorsList(r.getMirrorsList());
          ri.setOsType(os.trim());
          ri.setRepoId(r.getRepoId());
          ri.setRepoName(r.getRepoName());
          ri.setDistribution(r.getDistribution());
          ri.setComponents(r.getComponents());
          ri.setUnique(r.isUnique());
          ri.setTags(r.tags);

          repos.add(ri);
        }
      }
    }

    return repos;
  }


}

