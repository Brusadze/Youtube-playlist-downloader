import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.ls.LSOutput;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class mainform extends Container {
    private JTextArea textArea1;
    private JPanel panel1;
    private JButton button1;
    private JProgressBar progressBar1;
    private JLabel jlabel1;


    public mainform() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                progressBar1.setValue(0);
                progressBar1.setStringPainted(true);
                progressBar1.setMaximum(100);
                progressBar1.setMinimum(0);

                // Runs outside of the Swing UI thread
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            button1.setVisible(false);
                            getUrls();
                            for (String s : newList){
                                getTaskDone(s.substring(9, newList.get(0).indexOf("&")));
                            }
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        } catch (URISyntaxException ex) {
                            throw new RuntimeException(ex);
                        }
                    }

                }).start();
            }
        });
    }

    static File selectedFile;
    public static void main(String[] args) {

        JFrame frame = new JFrame("YoutubeDownloader");
        frame.setContentPane(new mainform().panel1);
        frame.setPreferredSize( new Dimension(600,600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Image icon = Toolkit.getDefaultToolkit().getImage("src/youtube.png");
        frame.setIconImage(icon);
        frame.pack();
        frame.setVisible(true);

        //Uncomment to use file chooser

       /* FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT files", "txt");
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setFileFilter(filter);
        jfc.setDialogTitle("choose source TXT file");
        jfc.showOpenDialog(frame);

        int returnValue = jfc.showOpenDialog(null);
        // int returnValue = jfc.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = jfc.getSelectedFile();
            System.out.println(selectedFile.getAbsolutePath());
        }*/

    }

    int index = 0;
    void getTaskDone(String _videoID) throws IOException, InterruptedException, URISyntaxException {
        /*HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.vevioz.com/api/button/mp3/" + _videoID))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        Document doc = Jsoup.parse(response.body(), "http://cdn-30.vevioz.com/");


        System.out.println(doc.select("a[href]").attr("abs:href"));*/

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://youtube-mp3-download1.p.rapidapi.com/dl?id=" + _videoID))
                .header("X-RapidAPI-Key", "c0fd7e398cmshc87145cf2049247p141290jsn61a43e9f92df")
                .header("X-RapidAPI-Host", "youtube-mp3-download1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        JSONObject  jsonObject = new JSONObject(response.body().toString());
        System.out.println(jsonObject.getString("link"));
        System.out.println(jsonObject.getString("title"));


        Desktop.getDesktop().browse(new URL(jsonObject.getString("link")).toURI());
        /*try (BufferedInputStream in = new BufferedInputStream(new URL(jsonObject.getString("link")).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream("D:\\Musikebi\\" + jsonObject.getString("title") + ".mp3")) {
            index++;
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            progressBar1.setValue(0);
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }progressBar1.setValue(100);
        } catch (IOException e) {
            // handle exception
        }*/
    }

    List<String> newList;
    List<String> names = new ArrayList<>();
    void getUrls() throws IOException {
        List<String> links = new ArrayList<>();
        //If used swing file chooser
    /*  File input = new File(selectedFile.getAbsolutePath());

        Document doc = Jsoup.parse(input, "UTF-8", "http://youtube.com/");*/
        Document doc = Jsoup.parse(new File("src/source.html"), "UTF-8", "http://youtube.com/");
        Elements elements = doc.select("a[id=wc-endpoint]");
        Elements spans = doc.select("span[id=video-title]").select("span.style-scope.ytd-playlist-panel-video-renderer");
        for (Element element : elements) {
            links.add(element.attr("href"));
        }
        for (Element span : spans){
            names.add(span.text());
        }

        System.out.println(links);
        System.out.println(spans.text());


       newList = links.stream()
                .distinct().collect(Collectors.toList());

        // Print the ArrayList with duplicates removed
        System.out.println("ArrayList with duplicates removed: "
                + newList);

        for (String s : newList){
            System.out.println(s.substring(9, newList.get(0).indexOf("&")));
            textArea1.append("\n " + s.substring(9, newList.get(0).indexOf("&")));
        }
        jlabel1.setText("Found " + newList.size() + " Video/Music");

    }

}
