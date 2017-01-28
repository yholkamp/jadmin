# Spark-Admin

Spark-Admin is a Spark Java powered framework to create simple backends for administration tasks. 
Inspired by the Ruby [Active Admin](https://github.com/activeadmin/activeadmin) library.

# Roadmap / to do list

* Include configurable authentication (currently only available by interacting with the internal Spark instance)
* Support multi-column primary keys
* Include smart support for foreign keys, i.e. display a `<select>` for foreign key columns
* Include CSRF tokens in each form
* Support database constraints, i.e. validate form input in the browser
* Improve the test coverage

# License

MIT License

Copyright (c) 2016 Yorick Holkamp

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.