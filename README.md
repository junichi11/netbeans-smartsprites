# About

This NetBeans Plugin is using [smartsprites](http://csssprites.org/).
Generate css sprite files(css and image) from css files and some images.

## Install

2ways

- clone this project and create nbm file
- Download nbm file from [Here](https://github.com/junichi11/netbeans-smartsprites/downloads)
 
## Usage

### SmartSprites

- Annotate your CSS with SmartSprites directives.(For more information, please see [smartsprites](http://csssprites.org/))
- Right-click on the folder(node) that has the CSS. 
- click SmartSprites.

### e.g.
     
     ├─css (right-click)
     │  ├─style.css
     │  └─style-hoge.css
     └─img
         ├─foo.png
         ├─bar.png
         ├─hoge.png
         ├─apple.gif
         ├─orange.gif
         └─strawberry.gif

### Generate CSS Sprite

Generate css sprite files from images.

- Right-click on the folder(node) that has some images. 
- click Generate CSS Sprite.
- Generate files in "csssprite" folder.

## License

The MIT License
