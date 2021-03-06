/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jooby;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Command {

  private boolean wait = true;

  private String name;

  private List<String> arguments;

  private File workdir;

  private Object alias;

  private Process process;

  private ProcessOutput out;

  private ProcessOutput err;

  public Command(final String alias, final String name, final List<String> args) {
    this.alias = alias;
    this.name = name;
    this.arguments = new ArrayList<String>(args);
  }

  public Command() {
  }

  public File getWorkdir() {
    return workdir;
  }

  public void setWorkdir(final File workdir) {
    if (this.workdir == null) {
      this.workdir = workdir;
    }
  }

  public boolean isWait() {
    return wait;
  }

  public void setWait(final boolean wait) {
    this.wait = wait;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public List<String> getArguments() {
    return arguments;
  }

  public void setArguments(final List<String> arguments) {
    this.arguments = arguments;
  }

  public void stop() throws InterruptedException {
    if (this.process != null) {
      this.out.stopIt();
      this.err.stopIt();
      this.process.destroy();
      this.process.waitFor();
      this.process = null;
      this.out = null;
      this.err = null;
    }
  }

  public void execute() throws IOException, InterruptedException {
    List<String> cmd = new ArrayList<String>();
    cmd.add(name);
    cmd.addAll(arguments);
    process = new ProcessBuilder(cmd)
        .directory(workdir)
        .start();
    // info
    out = new ProcessOutput(process.getInputStream(), false);
    out.start();
    // err
    err = new ProcessOutput(process.getErrorStream(), true);
    err.start();
    if (wait) {
      process.waitFor();
    }

  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    if (alias == null) {
      str.append(name);
      for (String arg : arguments) {
        str.append(" ").append(arg);
      }
    } else {
      str.append(alias);
    }
    return str.toString();
  }

  public String debug() {
    StringBuilder str = new StringBuilder();
    str.append(name);
    for (String arg : arguments) {
      str.append(" ").append(arg);
    }
    return str.toString();
  }
}
