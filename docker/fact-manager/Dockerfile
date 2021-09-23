FROM openjdk:8-jre-alpine

ARG jenkins_user
ARG jenkins_password
ARG jenkins_url


ARG machine_domain


ENV jenkins_project FactManager_release
ENV project_name fact-manager


#Set timezone.
ENV TZ=Europe/Madrid
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Create app directory
RUN mkdir -p /usr/share/${project_name}

# Change directory
WORKDIR /usr/share/${project_name}

#Copy configuration files.
COPY config/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

#Configure files
#RUN sed -i "s|PROJECT_HOME|/opt/${project_name}|g" /etc/supervisor/conf.d/supervisord.conf

#Add ssh keys
RUN mkdir -p /root/.ssh && \
	chmod 700 /root/.ssh && \
	touch /root/.ssh/authorized_keys && \
	chmod 664 /root/.ssh/authorized_keys && \
	touch /root/.ssh/config && \
	chmod 644 /root/.ssh/config && \
	echo -e "Host jenkins.biit-solutions.com" > /root/.ssh/config && \
	echo -e "StrictHostKeyChecking no" >> /root/.ssh/config && \
	echo -e "UserKnownHostsFile=/dev/null" >> /root/.ssh/config && \
	echo -e "Hostname jenkins.biit-solutions.com" >> /root/.ssh/config && \
	echo -e "User admin" >> /root/.ssh/config && \
	echo -e "IdentitiesOnly yes" >> /root/.ssh/config
	

#Copy execution script
COPY entrypoint.sh .
COPY factManager.jar /usr/share/${project_name}
	
#Download jar
RUN apk --no-cache add openssh-client curl supervisor  && \
	mv factManager.jar ./${project_name}.jar && \
	apk del openssh-client && \
	chmod 755 ./entrypoint.sh && \
	chmod 777 ./${project_name}.jar
	
# Ports
EXPOSE 8085

# EXEC
CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]
#ENTRYPOINT ["java","-jar","./fact-manager.jar"]
#ENTRYPOINT [ "./entrypoint.sh" ]
