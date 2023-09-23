print 'Create shared library'
AdminConfig.create('Library', AdminConfig.getid('/Cell:DefaultCell01/Node:DefaultNode01/Server:server1/'), '[[nativePath ""] [name "roller-lib"] [isolatedClassLoader false] [description ""] [classPath "/opt/IBM/WebSphere/libs/roller-lib.jar"]]')
AdminConfig.save()

print 'Create shared library reference'
AdminConfig.create('Classloader', '(cells/DefaultCell01/nodes/DefaultNode01/servers/server1|server.xml#ApplicationServer_1183122130078)', '[[mode PARENT_FIRST]]')
classLoad1 = AdminConfig.list('Classloader', AdminConfig.getid( '/Cell:DefaultCell01/Node:DefaultNode01/Server:server1/'))
AdminConfig.create('LibraryRef', classLoad1, [['libraryName', 'roller-lib']])
AdminConfig.save()