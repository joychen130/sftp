/*
 * Copyright 2015-2018 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.app.sftp.sink;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cloud.stream.config.SpelExpressionConverterConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.test.util.TestUtils;

/**
 * @author David Turanski
 * @author Gary Russell
 * @author Artem Bilan
 */
public class SftpSinkPropertiesTests {

	@Test
	public void remoteDirCanBeCustomized() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of("sftp.remoteDir:/remote")
				.applyTo(context);
		context.register(Conf.class);
		context.refresh();
		SftpSinkProperties properties = context.getBean(SftpSinkProperties.class);
		assertThat(properties.getRemoteDir(), equalTo("/remote"));
		context.close();
	}

	@Test
	public void autoCreateDirCanBeDisabled() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of("sftp.autoCreateDir:false")
				.applyTo(context);
		context.register(Conf.class);
		context.refresh();
		SftpSinkProperties properties = context.getBean(SftpSinkProperties.class);
		assertTrue(!properties.isAutoCreateDir());
		context.close();
	}

	@Test
	public void tmpFileSuffixCanBeCustomized() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of("sftp.tmpFileSuffix:.foo")
				.applyTo(context);
		context.register(Conf.class);
		context.refresh();
		SftpSinkProperties properties = context.getBean(SftpSinkProperties.class);
		assertThat(properties.getTmpFileSuffix(), equalTo(".foo"));
		context.close();
	}

	@Test
	public void tmpFileRemoteDirCanBeCustomized() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of("sftp.temporaryRemoteDir:/foo")
				.applyTo(context);
		context.register(Conf.class);
		context.refresh();
		SftpSinkProperties properties = context.getBean(SftpSinkProperties.class);
		assertThat(properties.getTemporaryRemoteDir(), equalTo("/foo"));
		context.close();
	}

	@Test
	public void remoteFileSeparatorCanBeCustomized() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of("sftp.remoteFileSeparator:\\")
				.applyTo(context);
		context.register(Conf.class);
		context.refresh();
		SftpSinkProperties properties = context.getBean(SftpSinkProperties.class);
		assertThat(properties.getRemoteFileSeparator(), equalTo("\\"));
		context.close();
	}

	@Test
	public void useTemporaryFileNameCanBeCustomized() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of("sftp.useTemporaryFilename:false")
				.applyTo(context);
		context.register(Conf.class);
		context.refresh();
		SftpSinkProperties properties = context.getBean(SftpSinkProperties.class);
		assertFalse(properties.isUseTemporaryFilename());
		context.close();
	}

	@Test
	public void fileExistsModeCanBeCustomized() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of("sftp.mode:FAIL")
				.applyTo(context);
		context.register(Conf.class);
		context.refresh();
		SftpSinkProperties properties = context.getBean(SftpSinkProperties.class);
		assertThat(properties.getMode(), equalTo(FileExistsMode.FAIL));
		context.close();
	}

	@Test
	public void knownHostsExpression() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of(
				"sftp.factory.known-hosts-expression = @systemProperties[\"user.home\"] + \"/.ssh/known_hosts\"",
				"sftp.factory.cache-sessions = true")
				.applyTo(context);
		context.register(Factory.class);
		context.refresh();
		SessionFactory<?> sessionFactory = context.getBean(SessionFactory.class);
		assertThat((String) TestUtils.getPropertyValue(sessionFactory, "sessionFactory.knownHosts"), endsWith(
				"/.ssh/known_hosts"));
		context.close();
	}

	@Configuration
	@EnableConfigurationProperties(SftpSinkProperties.class)
	static class Conf {

	}

	@Configuration
	@EnableConfigurationProperties(SftpSinkProperties.class)
	@EnableIntegration
	@Import({ SftpSinkSessionFactoryConfiguration.class, SpelExpressionConverterConfiguration.class })
	static class Factory {

	}

}
