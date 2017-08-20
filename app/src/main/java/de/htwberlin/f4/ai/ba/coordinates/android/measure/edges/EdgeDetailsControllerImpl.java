package de.htwberlin.f4.ai.ba.coordinates.android.measure.edges;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.carol.bvg.R;

import de.htwberlin.f4.ai.ba.coordinates.android.BaseActivity;
import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;

/**
 * Created by benni on 13.08.2017.
 */

public class EdgeDetailsControllerImpl implements EdgeDetailsController {

    private EdgeDetailsView view;

    private Node startNode;
    private Node targetNode;
    private Edge edge;

    @Override
    public void setView(EdgeDetailsView view) {
        this.view = view;
    }

    @Override
    public void setNodes(String startNodeId, String targetNodeId) {
        DatabaseHandler databaseHandler = DatabaseHandlerFactory.getInstance(view.getContext());
        startNode = databaseHandler.getNode(startNodeId);
        targetNode = databaseHandler.getNode(targetNodeId);
    }

    @Override
    public void onDeleteClicked() {
        if (edge != null) {
            // ask the user if the way was handycap friendly and save measurement data afterwards
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
            alertDialogBuilder.setTitle("Weg löschen");
            alertDialogBuilder.setMessage("Sind Sie sicher, dass Sie den Weg löschen möchten?");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setIcon(R.drawable.trash);

            alertDialogBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    deleteEdge();
                    dialog.dismiss();
                    view.finish();
                }
            });

            alertDialogBuilder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private void deleteEdge() {
        if (edge != null) {
            DatabaseHandler databaseHandler = DatabaseHandlerFactory.getInstance(view.getContext());
            databaseHandler.deleteEdge(edge);
        }
    }

    @Override
    public void onSaveClicked() {
        if (edge != null) {
            DatabaseHandler databaseHandler = DatabaseHandlerFactory.getInstance(view.getContext());
            databaseHandler.updateEdge(edge);
            //BaseActivity activity = (BaseActivity) view;
            //activity.loadMeasurement();
            view.finish();
        }
    }

    @Override
    public void onHandycapChanged(boolean handycapFriendly) {
        if (edge != null) {
            edge.setAccessibility(handycapFriendly);
        }
    }

    @Override
    public void onEdgeInfoChanged(String info) {
        if (edge != null) {
            edge.setAdditionalInfo(info);
        }
    }


    @Override
    public void onResume() {
        if (startNode != null && targetNode != null) {
            view.updateStartNodeInfo(startNode);
            view.updateTargetNodeInfo(targetNode);

            DatabaseHandler databaseHandler = DatabaseHandlerFactory.getInstance(view.getContext());
            edge = databaseHandler.getEdge(startNode, targetNode);

            view.updateEdgeInfo(edge);
        }
    }
}
