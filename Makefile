platforms = linux/amd64,linux/arm64/v8

ROOT_DIR:=$(shell dirname $(realpath $(firstword $(MAKEFILE_LIST))))

define checkstyle
#!/bin/sh
make style
exit $?
endef
export checkstyle

.PHONY=setup
setup:
	@cd $(ROOT_DIR) && \
echo "$$checkstyle" > .git/hooks/pre-push && chmod 755 .git/hooks/pre-push && echo "setup completed"

.PHONY=version
version:
	@cd $(ROOT_DIR) && \
VERSION="$$(./mvnw -q -Dexec.executable=echo -Dexec.args='$${project.version}' --non-recursive exec:exec -P-testtools)" && \
echo "$${VERSION}"

.PHONY=clean
clean:
	@cd $(ROOT_DIR) && \
./mvnw --no-transfer-progress clean -P-testtools

.PHONY=test
test: clean
	@cd $(ROOT_DIR) && \
./mvnw --no-transfer-progress test -Ptesttools

.PHONY=package
package: clean
	@cd $(ROOT_DIR) && \
./mvnw --batch-mode --no-transfer-progress package -P-testtools -DskipTests=true

.PHONY=style
style:
	@./mvnw checkstyle:check

.PHONY=upgrade
upgrade:
	@VERSION="$$(./mvnw -q -Dexec.executable=echo -Dexec.args='$${project.version}' --non-recursive exec:exec -P-testtools)" && \
NEXTVERSION=$$(echo $${VERSION} | awk -F. -v OFS=. '{$$NF += 1 ; print}') && \
echo "$${NEXTVERSION}" | ./mvnw versions:set -DgenerateBackupPoms=false
