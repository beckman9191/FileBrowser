import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.io.File
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.stage.DirectoryChooser
import java.io.FileInputStream
import javax.swing.JFileChooser
import kotlin.system.exitProcess



class Main : Application()  {


    override fun start(stage: Stage) {

        // determine starting directory
        // this will be the "test" subfolder in your project directory
        val dir = File("${System.getProperty("user.dir")}/test/")
        var currdir = dir
        var currpath = currdir.toString()
        var tree = ListView<String>()
        var fmap = mutableMapOf<String, File>()
        var list = mutableListOf<File>()
        var is_hidden = true
        var is_root = false
        var img = ImageView()
        var textarea = TextArea("")





        // create the root of the scene graph
        // BorderPane supports placing children in regions around the screen
        val layout = BorderPane()

        // top: menu bar
        val menuBar = MenuBar()

        // create a toolbar
        val toolbar = ToolBar()

        // create a vbox to store both menubar and toolbar
        val vbox = VBox()


        // create toolbar buttons
        val path = File("${System.getProperty("user.dir")}/src/main/resources")

        val home = Button("Home")
        println(path.toString())
        val homeImg = ImageView(Image(FileInputStream(path.toString() + "/home.png")))
        homeImg.fitWidth = 20.0
        homeImg.fitHeight = 20.0
        home.graphic = homeImg

        val prev = Button("Prev")
        val prevImg = ImageView(Image(FileInputStream(path.toString() + "/prev.png")))
        prevImg.fitWidth = 20.0
        prevImg.fitHeight = 20.0
        prev.graphic = prevImg

        val next = Button("Next")
        val nextImg = ImageView(Image(FileInputStream(path.toString() + "/next.png")))
        nextImg.fitWidth = 20.0
        nextImg.fitHeight = 20.0
        next.graphic = nextImg

        val delete = Button("Delete")
        val deleteImg = ImageView(Image(FileInputStream(path.toString() + "/delete.png")))
        deleteImg.fitWidth = 20.0
        deleteImg.fitHeight = 20.0
        delete.graphic = deleteImg

        val rename = Button("Rename")
        val renameImg = ImageView(Image(FileInputStream(path.toString() + "/rename.png")))
        renameImg.fitWidth = 20.0
        renameImg.fitHeight = 20.0
        rename.graphic = renameImg

        val move = Button("Move")
        val moveImg = ImageView(Image(FileInputStream(path.toString() + "/move.png")))
        moveImg.fitWidth = 20.0
        moveImg.fitHeight = 20.0
        move.graphic = moveImg

        // add the toolbar buttons to the toolbar
        toolbar.items.add(home)
        toolbar.items.add(prev)
        toolbar.items.add(next)
        toolbar.items.add(delete)
        toolbar.items.add(rename)
        toolbar.items.add(move)


        // create menu buttons
        val fileMenu = Menu("File")
        val viewMenu = Menu("View")
        val actionMenu = Menu("Actions")
        val optionMenu = Menu("Options")

        // create the drop-down options
        val fileQuit = MenuItem("Quit")
        val viewPrev = MenuItem("Prev")
        val viewNext = MenuItem("Next")
        val actionRename = MenuItem("Rename")
        val actionDelete = MenuItem("Delete")
        val actionMove = MenuItem("Move")
        val optionToggle = RadioMenuItem("Show Hidden")



        // add the menu buttons to the menu bar
        menuBar.menus.add(fileMenu)
        menuBar.menus.add(viewMenu)
        menuBar.menus.add(actionMenu)
        menuBar.menus.add(optionMenu)

        // add the drop-down options
        fileMenu.items.add(fileQuit)

        viewMenu.items.add(viewPrev)
        viewMenu.items.add(viewNext)

        actionMenu.items.add(actionRename)
        actionMenu.items.add(actionDelete)
        actionMenu.items.add(actionMove)

        optionMenu.items.add(optionToggle)

        // put the toolbar and the menubar into the vbox
        vbox.children.add(menuBar)
        vbox.children.add(toolbar)

        // helper function:
        fun DisplayFile(filelist: MutableList<File>, is_hidden: Boolean, dir:File) {
            fmap.clear()
            list.clear()
            tree.items.clear()
            if(dir != null) {
                for(item in dir.listFiles()) {
                    list.add(item)
                    fmap.put(item.name, item)

                }
            }

            for(item in list) {
                var name = item.name
                if(item.isDirectory) {
                    name += "/"
                }
                if(name[0] == '.' && is_hidden == false) {
                    tree.items.add(name)
                } else if(name[0] != '.') {
                    tree.items.add(name)
                }

            }
        }

        fun showtext(file: String) {
            var target = fmap?.get(file)
            if(target != null) {
                var content = target.bufferedReader()
                textarea.text = content.use { it.readText() }
                layout.center = textarea
                layout.bottom = Label(target.toString())

            }
        }

        fun showimage(file: String) {
            var target = fmap?.get(file)
            if(target != null) {
                var content = FileInputStream(target)
                var myimage = Image(content)
                content.close()
                img.image = myimage
                layout.center = img
                img.fitWidth = 500.0
                img.fitHeight = 300.0
                img.isPreserveRatio = true

                layout.bottom = Label(target.toString())
            }
        }

        fun ScrollUpandDown() {
            var fname = tree.selectionModel.selectedItems[0]
            if(fname.endsWith(".txt") || fname.endsWith(".md")) { // show text
                showtext(fname)
            } else if(fname.endsWith(".jpg") || fname.endsWith(".png") || fname.endsWith(".bmp")) { // show image
                showimage(fname)
            } else if(fname.endsWith("/")) {
                textarea.text = ""
                layout.center = textarea


                layout.bottom = Label(currpath + "\\" + fname.substring(0, fname.length - 1))
            }
        }

        fun EnterDirectory() {
            if(!tree.selectionModel.isEmpty) {
                var fname = tree.selectionModel.selectedItems[0]
                if(fname.endsWith("/")) {
                    textarea.text = ""
                    layout.center = textarea

                    layout.bottom = Label(currpath + "\\" + fname.substring(0, fname.length - 1))

                    currdir = File(currdir.toString() + "/" + fname + "/")
                    currpath = currpath + "\\" + fname.substring(0, fname.length - 1)
                    DisplayFile(list, is_hidden, currdir)
                    is_root = false
                }
            }
        }

        fun previousDirectory() {
            if(!is_root) {
                val lastindex_path = currpath.lastIndexOf('\\')

                val lastindex_dir = currdir.toString().lastIndexOf('\\')

                currpath = currpath.substring(0, lastindex_path)
                currdir = File(currdir.toString().substring(0, lastindex_dir))
                textarea.text = ""
                layout.center = textarea
                layout.bottom = Label(currpath)
                if(currpath.endsWith(":")) {
                    layout.bottom = Label(currpath + "\\")
                    currdir = File(currdir.toString().substring(0, lastindex_dir) + "/")
                    is_root = true
                }
                DisplayFile(list, is_hidden, currdir)
            }
        }

        fun renameFile(origin: String, new: String) {

            // get file references
            val file1 = File(origin)
            val file2 = File(new)



            // check for errors
            if (!file1.exists()) {
                println("Error: $file1 does not exist")
                exitProcess(0)
            }
            if (file2.exists()) {
                println("Error: $file2 already exists")
                exitProcess(0)
            }
            val success = file1.renameTo(file2)
            if (success) {
                println("Successfully renamed $file1 to $file2")
            } else {
                println("Error renaming $file1 to $file2")
            }
        }

        fun deleteFile(filename: String) {
            val file = File(filename)
            if (!file.exists()) {
                println("Error: $file does not exist")
                exitProcess(0)
            }

            // println("file exist? " + file.exists())
            // println("file writable? " + file.canWrite())
            // val success = file.delete()
            val success = file.deleteRecursively()




            if(success) {
                println("Successfully removed $file")
                textarea.text = ""
                layout.center = textarea
            } else {
                println("Error removed $file")
            }

        }

        fun moveFile(filename: String) {
            val dc = DirectoryChooser()
            val directory = dc.showDialog(stage)

            if(directory != null) {
                println(directory)
                var newdirectory = directory.toString() + "/" + filename
                println("new directory is " + newdirectory)
                var file = File(currdir.toString() + "/" + filename)
                println("file path is " + file.toString())
                file.copyTo(File(newdirectory))

                val success = file.delete()
                if(!success) {
                    println("Error removed $file")
                }

            }

        }


        // Initial File view
        DisplayFile(list, is_hidden, dir)
        layout.bottom = Label(currpath)


        // handle default user action aka press
        fileQuit.setOnAction { event ->
            exitProcess(0)

        }

        // handle Prev menu action
        viewPrev.setOnAction { event ->
            previousDirectory()
        }

        // handle Next menu action
        viewNext.setOnAction { event ->
            EnterDirectory()
        }

        // handle Rename menu action
        actionRename.setOnAction { event ->
            if(!tree.selectionModel.isEmpty) {
                var fname = tree.selectionModel.selectedItems[0]
                var dialog = TextInputDialog("rename")
                dialog.title = "Rename"
                dialog.headerText = "Do you wish to rename " + fname + " ?"
                dialog.contentText = "Please enter the new name of the file:"

                dialog.showAndWait()

                if(dialog.result != null) {
                    var newname = dialog.result

                    renameFile(currdir.toString() + "/" + fname, currdir.toString() + "/" + newname)
                    DisplayFile(list, is_hidden, currdir)
                }


            }
        }

        // hanle Delete menu action
        actionDelete.setOnAction { event ->
            if(!tree.selectionModel.isEmpty) {
                var fname = tree.selectionModel.selectedItems[0]

                var dialog = Alert(Alert.AlertType.CONFIRMATION,
                    "Do you wish to delete " + fname.toString() + " ?",
                    ButtonType.YES, ButtonType.NO)

                dialog.showAndWait()

                if(dialog.result == ButtonType.YES) {
                    deleteFile(currdir.toString() + "/" + fname)
                    DisplayFile(list, is_hidden, currdir)
                    // tree.items.remove(fname)

                }
            }
        }

        // handle Move menu action
        actionMove.setOnAction { event ->
            if(!tree.selectionModel.isEmpty) {
                var fname = tree.selectionModel.selectedItems[0]

                moveFile(fname)
                DisplayFile(list, is_hidden, currdir)
            }
        }

        // handle toggle menu action
        optionToggle.setOnAction { event ->
            if(is_hidden) {
                is_hidden = false
            } else {
                is_hidden = true
            }

            DisplayFile(list, is_hidden, currdir)


        }


        // handle HOME button
        home.setOnAction { event ->

            currpath = dir.toString()
            currdir = dir
            textarea.text = ""
            layout.center = textarea
            layout.bottom = Label(currpath)
            DisplayFile(list, is_hidden, dir)
            is_root = false
        }

        // handle PREV button
        prev.setOnAction { event ->
            previousDirectory()

        }

        // handle NEXT button
        next.setOnAction { event ->
            EnterDirectory()

        }

        // handle DELETE button
        delete.setOnAction { event ->
            if(!tree.selectionModel.isEmpty) {
                var fname = tree.selectionModel.selectedItems[0]

                var dialog = Alert(Alert.AlertType.CONFIRMATION,
                        "Do you wish to delete " + fname.toString() + " ?",
                                   ButtonType.YES, ButtonType.NO)

                dialog.showAndWait()

                if(dialog.result == ButtonType.YES) {
                    deleteFile(currdir.toString() + "/" + fname)
                    DisplayFile(list, is_hidden, currdir)
                    // tree.items.remove(fname)

                }
            }
        }

        // handle RENAME button
        rename.setOnAction { event ->
            if(!tree.selectionModel.isEmpty) {
                var fname = tree.selectionModel.selectedItems[0]
                var dialog = TextInputDialog("rename")
                dialog.title = "Rename"
                dialog.headerText = "Do you wish to rename " + fname + " ?"
                dialog.contentText = "Please enter the new name of the file:"

                dialog.showAndWait()

                if(dialog.result != null) {
                    var newname = dialog.result

                    renameFile(currdir.toString() + "/" + fname, currdir.toString() + "/" + newname)
                    DisplayFile(list, is_hidden, currdir)
                }


            }



        }

        // handle Move button
        move.setOnAction { event ->
            if(!tree.selectionModel.isEmpty) {
                var fname = tree.selectionModel.selectedItems[0]

                moveFile(fname)
                DisplayFile(list, is_hidden, currdir)
            }
        }

        // handle ENTER/BACKSPACE/DELETE key
        tree.setOnKeyPressed { event ->

            if(!tree.selectionModel.isEmpty && event.code == KeyCode.ENTER) {
                EnterDirectory()
            } else if(event.code == KeyCode.BACK_SPACE || event.code == KeyCode.DELETE) {
                previousDirectory()
            } else if(!tree.selectionModel.isEmpty && (event.code == KeyCode.DOWN || event.code == KeyCode.UP)) {
                ScrollUpandDown()
            }
        }



        // handle mouse clicked action
        tree.setOnMouseClicked { event ->
            // println("Pressed ${event.button}")
            var fname = tree.selectionModel.selectedItems[0]
            if(fname.endsWith(".txt") || fname.endsWith(".md")) { // show text
                showtext(fname)
            } else if(fname.endsWith(".jpg") || fname.endsWith(".png") || fname.endsWith(".bmp")) { // show image
                showimage(fname)
            } else if(fname.endsWith("/")) {
                textarea.text = ""
                layout.center = textarea


                layout.bottom = Label(currpath + "\\" + fname.substring(0, fname.length - 1))


                if(event.clickCount == 2) {
                    currdir = File(currdir.toString() + "/" + fname + "/")
                    currpath = currpath + "\\" + fname.substring(0, fname.length - 1)
                    DisplayFile(list, is_hidden, currdir)
                    is_root = false
                }


            }
        }

        // build the scene graph
        layout.top = vbox
        layout.left = tree

        // create and show the scene
        val scene = Scene(layout)
        stage.width = 800.0
        stage.height = 500.0
        stage.scene = scene
        stage.show()
    }
}